package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import journeymap.api.v2.client.display.Displayable;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.fullscreen.IThemeButton;
import journeymap.api.v2.client.model.MapPolygonWithHoles;
import journeymap.api.v2.client.util.PolygonHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.compat.ftbchunks.ClaimedChunk;
import me.frankv.jmi.compat.ftbchunks.FTBChunksCompatStates;
import me.frankv.jmi.compat.ftbchunks.OverlayUtil;
import me.frankv.jmi.compat.ftbchunks.claimingmode.ClaimingMode;
import me.frankv.jmi.util.BackgroundWorker;
import me.frankv.jmi.util.OverlayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.function.Consumer;

import static me.frankv.jmi.util.OverlayHelper.removeOverlay;
import static me.frankv.jmi.util.OverlayHelper.removeOverlays;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;
import static me.frankv.jmi.util.OverlayHelper.showOverlays;

@Slf4j
public enum ClaimedChunksOverlay implements ToggleableOverlay {
    INSTANCE;

    private static final int POLYGON_Y = 10;

    private final Minecraft mc = Minecraft.getInstance();
    private final Queue<ClaimedChunk> queue = new LinkedList<>();
    private final BackgroundWorker<PolygonJob, List<List<MapPolygonWithHoles>>> worker =
            new BackgroundWorker<>("JMI-Claim-Polygons", ClaimedChunksOverlay::computePolygons);
    @Getter
    private final int order = 1;
    @Getter
    private final String buttonLabel = "jmi.toggleable_overlay.ftbchunks";
    private ClientConfig clientConfig;
    @Getter
    private boolean activated = true;

    private int tick = 1;
    private FTBChunksCompatStates states;
    private boolean shouldToggleAfterOff = false;
    private boolean jmMappingStarted = false;

    private record PolygonJob(long generation, ResourceKey<Level> dim, UUID teamId, TeamClaimGraph.RebuildPlan plan) {
    }

    public void init(ClientConfig clientConfig, FTBChunksCompatStates states) {
        this.clientConfig = clientConfig;
        this.states = states;

        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
    }

    public void onClientTick() {
        if (!clientConfig.getFtbChunks()) return;
        worker.drain(this::applyResult);
        if (mc.level == null) return;
        if (!jmMappingStarted) return;

        if (tick < 0 || tick % 4 != 0) {
            tick++;
            return;
        }

        processQueue();
        submitRebuilds(mc.level.dimension());
        tick = 1;
    }

    public void onReset() {
        queue.clear();
        jmMappingStarted = false;
    }

    private void processQueue() {
        for (var data : queue) {
            final var pos = data.chunkDimPos();
            final var chunkPos = pos.getChunkPos();
            final var existing = states.getChunkData().get(pos);

            if (data.getTeam().isEmpty()) {  // When a team of data is empty means this action is unclaiming
                if (existing == null) {
                    log.warn("Failed to remove an unknown claimed chunk. dim: {}, chunk: {}, player_dim: {}",
                            pos.dimension(), chunkPos, mc.level.dimension());
                    continue;
                }
                states.getChunkData().remove(pos);
                states.teamClaims(pos.dimension(), existing.teamId()).getGraph().remove(chunkPos);
                continue;
            }

            if (existing != null && !existing.teamId().equals(data.teamId())) {
                states.teamClaims(pos.dimension(), existing.teamId()).getGraph().remove(chunkPos);
            }
            states.getChunkData().put(pos, data);
            states.teamClaims(pos.dimension(), data.teamId()).getGraph().add(chunkPos);

            if (existing != null && ClaimingMode.INSTANCE.isActivated()) {
                showForceLoaded(pos, false);
                showForceLoaded(pos, true);
            }
        }
        queue.clear();
    }

    private void submitRebuilds(ResourceKey<Level> dim) {
        for (var entry : states.claimsIn(dim).entrySet()) {
            final var plan = entry.getValue().getGraph().plan();
            if (plan == null) continue;
            worker.submit(new PolygonJob(states.getGeneration(), dim, entry.getKey(), plan));
        }
    }

    private static List<List<MapPolygonWithHoles>> computePolygons(PolygonJob job) {
        final var result = new ArrayList<List<MapPolygonWithHoles>>(job.plan().newComponents().size());
        for (var component : job.plan().newComponents()) {
            result.add(PolygonHelper.createChunksPolygon(component, POLYGON_Y));
        }
        return result;
    }

    private void applyResult(BackgroundWorker.Result<PolygonJob, List<List<MapPolygonWithHoles>>> result) {
        final var job = result.job();
        if (job.generation() != states.getGeneration()) return;

        final var teamClaims = states.findTeamClaims(job.dim(), job.teamId()).orElse(null);
        if (teamClaims == null) return;

        final var ids = teamClaims.getGraph().commit(job.plan());
        final var visible = isDimensionVisible(job.dim());

        for (var staleId : job.plan().staleComponents()) {
            final var old = teamClaims.getOverlays().remove(staleId);
            if (old != null && visible) removeOverlays(old);
        }

        if (result.error() != null) {
            log.error("Failed to build claim polygons for team {}", job.teamId(), result.error());
            return;
        }

        final var team = ClientTeamManagerImpl.getInstance().getTeam(job.teamId()).orElse(null);
        if (team == null) return;

        for (var i = 0; i < ids.size(); i++) {
            final var overlays = new ArrayList<PolygonOverlay>();
            for (var polygon : result.value().get(i)) {
                overlays.add(createOverlay(job.dim(), team, polygon));
            }
            teamClaims.getOverlays().put(ids.get(i), overlays);
            if (visible && activated) showOverlays(overlays);
        }
    }

    private PolygonOverlay createOverlay(ResourceKey<Level> dim, ClientTeam team, MapPolygonWithHoles polygon) {
        final var overlay = new PolygonOverlay(Constants.MOD_ID, dim,
                states.getShapeProps(team, clientConfig.getClaimedChunkOverlayOpacity().floatValue()),
                polygon);

        overlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(team.getDisplayName())
                .setOverlayListener(new ClaimedChunkOverlayListener(team.getTeamId(), states, overlay))
                .setTextProperties(states.getTextProps(team));

        return overlay;
    }

    private boolean isDimensionVisible(ResourceKey<Level> dim) {
        return jmMappingStarted && mc.level != null && mc.level.dimension().equals(dim);
    }

    public void showForceLoadedByArea(boolean show) {
        final var level = mc.level;
        if (level == null) return;

        if (!show) {
            removeOverlays(states.getForceLoadedOverlays().values());
            states.getForceLoadedOverlays().clear();
            return;
        }

        ClaimingMode.INSTANCE.getArea().forEach(p -> {
            final var chunkDimPos = new ChunkDimPos(level.dimension(), p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        });
    }

    public void onClaiming(boolean off) {
        if (!off && activated) return;
        if (!off) {
            toggleOverlay();
            shouldToggleAfterOff = true;
        } else if (shouldToggleAfterOff) {
            toggleOverlay();
            shouldToggleAfterOff = false;
        }
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!states.getChunkData().containsKey(chunkDimPos)) return;
        var data = states.getChunkData().get(chunkDimPos);

        if (show && data.forceLoaded() && !states.getForceLoadedOverlays().containsKey(chunkDimPos)) {
            var claimedOverlay = ClaimingMode.INSTANCE.forceLoadedPolygon(chunkDimPos, data, states);
            showOverlay(claimedOverlay);
            states.getForceLoadedOverlays().put(chunkDimPos, claimedOverlay);
        } else if (!show && states.getForceLoadedOverlays().containsKey(chunkDimPos)) {
            removeOverlay(states.getForceLoadedOverlays().get(chunkDimPos));
            states.getForceLoadedOverlays().remove(chunkDimPos);
        }
    }

    private void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        var teamId = event.getTeam().getTeamId();
        var clientTeam = ClientTeamManagerImpl.getInstance().getTeam(teamId).orElse(null);
        if (clientTeam == null) return;

        Optional.ofNullable(states.getShapeProperties().get(teamId))
                .ifPresent(prop -> prop.setFillColor(clientTeam.getColor()).setStrokeColor(clientTeam.getColor()));

        Optional.ofNullable(states.getTextProperties().get(teamId))
                .ifPresent(prop -> prop.setColor(OverlayUtil.getTeamTextColor(clientTeam)));

        final var displayName = clientTeam.getDisplayName();
        states.getClaims().forEach((dim, teams) -> {
            final var teamClaims = teams.get(teamId);
            if (teamClaims == null) return;
            final var visible = isDimensionVisible(dim) && activated;
            teamClaims.allOverlays().forEach(polygon -> {
                polygon.setTitle(displayName);
                if (visible) showOverlay(polygon);
            });
        });
    }

    @Override
    public void onToggle(IThemeButton button) {
        if (ClaimingMode.INSTANCE.isActivated()) return;
        toggleOverlay();
        button.setToggled(activated);
    }

    private void toggleOverlay() {
        Consumer<Displayable> action = activated ? OverlayHelper::removeOverlay : OverlayHelper::showOverlay;

        if (mc.level != null) {
            states.overlaysIn(mc.level.dimension()).forEach(action);
        }

        activated = !activated;
    }

    private void showCachedOverlays() {
        final var level = mc.level;
        if (level == null || !activated) return;
        states.overlaysIn(level.dimension()).forEach(OverlayHelper::showOverlay);
    }

    public void onJMMapping(Event.JMMappingEvent e) {
        switch (e.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                tick = -20;
                jmMappingStarted = true;
                showCachedOverlays();
            }

            case MAPPING_STOPPED -> {
                jmMappingStarted = false;
                states.clearForceLoaded();
            }
        }
    }

    public void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!clientConfig.getFtbChunks()) return;
        queue.offer(ClaimedChunk.create(dim, chunk, teamId));
        tick = tick > 0 ? 1 : -20;
    }

    @Override
    public ResourceLocation getButtonIconName() {
        return new ResourceLocation(Constants.MOD_ID, "images/ftb.png");
    }

}
