package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.api.property.TeamProperties;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import journeymap.api.v2.client.display.Displayable;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.fullscreen.IThemeButton;
import journeymap.api.v2.client.util.PolygonHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.compat.ftbchunks.ClaimedChunk;
import me.frankv.jmi.compat.ftbchunks.FTBChunksCompatStates;
import me.frankv.jmi.compat.ftbchunks.PolygonWrapper;
import me.frankv.jmi.compat.ftbchunks.claimingmode.ClaimingMode;
import me.frankv.jmi.util.OverlayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Consumer;

import static me.frankv.jmi.util.OverlayHelper.*;

@Slf4j
public enum ClaimedChunksOverlay implements ToggleableOverlay {
    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();
    private final Queue<ClaimedChunk> queue = new LinkedList<>();
    @Getter
    private final int order = 1;
    @Getter
    private final String buttonLabel = "FTBChunks Overlay";
    private ClientConfig clientConfig;
    @Getter
    private boolean activated = true;

    private int tick = 1;
    private FTBChunksCompatStates states;
    private boolean shouldToggleAfterOff = false;
    private boolean jmMappingStarted = false;

    public void init(ClientConfig clientConfig, FTBChunksCompatStates states) {
        this.clientConfig = clientConfig;
        this.states = states;

        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
    }

    public void onClientTick() {
        if (!clientConfig.getFtbChunks()) return;
        if (mc.level == null) return;
        if (!jmMappingStarted) return;

        if (tick < 0 || tick % 4 != 0) {
            tick++;
            return;
        }

        var modifiedTeamIds = new HashSet<UUID>();

        for (var data : queue) {
            if (data.getTeam().isEmpty()) {
                var removed = states.getChunkData().remove(data.chunkDimPos());
                Optional.ofNullable(removed).ifPresentOrElse(o -> modifiedTeamIds.add(o.teamId()),
                        () -> log.warn("Failed to remove an unknown claimed chunk. dim: {}, chunk: {}, player_dim: {}",
                                data.chunkDimPos().dimension(), data.chunkDimPos().chunkPos(), mc.level.dimension()));

                continue;
            }

            var existent = states.getChunkData().get(data.chunkDimPos());
            if (existent != null) {
                modifiedTeamIds.add(existent.teamId());
                replaceChunk(data);
            } else {
                states.getChunkData().put(data.chunkDimPos(), data);
            }
            modifiedTeamIds.add(data.teamId());
        }

        if (!modifiedTeamIds.isEmpty()) {
            var polygon = createPolygon(mc.level, modifiedTeamIds);
            modifiedTeamIds.forEach(id -> updateOverlays(id, polygon.getOrDefault(id, Set.of())));
        }

        queue.clear();
        tick = 1;
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

    private Map<UUID, Set<PolygonWrapper>> createPolygon(Level level) {
        return createPolygon(level, null);
    }

    private Map<UUID, Set<PolygonWrapper>> createPolygon(Level level, Set<UUID> teamIds) {
        var pos = new HashMap<UUID, Set<ChunkPos>>();
        var overlays = new HashMap<UUID, Set<PolygonWrapper>>();

        states.getChunkData().values().stream()
                .filter(data -> data.chunkDimPos().dimension().equals(level.dimension()))
                .filter(o -> teamIds == null || teamIds.contains(o.teamId()))
                .forEach(o -> pos.computeIfAbsent(o.teamId(), k -> new HashSet<>())
                        .add(o.chunkDimPos().chunkPos()));

        for (var teamId : pos.keySet()) {
            var polygons = PolygonHelper.createChunksPolygon(pos.get(teamId), 10);
            var team = ClientTeamManagerImpl.getInstance().getTeam(teamId).orElse(null);
            if (team == null) continue;

            for (var polygon : polygons) {

                var overlay = new PolygonOverlay(Constants.MOD_ID, level.dimension(), states.getShapeProps(team,
                        clientConfig.getClaimedChunkOverlayOpacity().floatValue()),
                        polygon);

                overlay.setOverlayGroupName("Claimed Chunks")
                        .setLabel(team.getDisplayName())
//                        .setTitle(team.getDisplayName())
                        .setOverlayListener(new ClaimedChunkOverlayListener(states, overlay))
                        .setTextProperties(states.getTextProps(team));

                overlays.computeIfAbsent(teamId, k -> new HashSet<>()).add(new PolygonWrapper(overlay));
            }

        }

        return overlays;
    }

    private void updateOverlays(UUID teamId, Set<PolygonWrapper> newOverlays) {
        var oldOverlays = Set.copyOf(states.getTeamOverlays().getOrDefault(teamId, Set.of()));
        if (oldOverlays.isEmpty()) {
            states.getTeamOverlays().put(teamId, newOverlays);
            if (!activated) return;
            newOverlays.forEach(o -> showOverlay(o.polygon()));
            return;
        }

        var addOverlays = new HashSet<>(newOverlays);
        var rmvOverlays = new HashSet<>(oldOverlays);
        rmvOverlays.removeAll(newOverlays);
        addOverlays.removeAll(oldOverlays);

        var newSet = new HashSet<>(oldOverlays);
        newSet.removeAll(rmvOverlays);
        newSet.addAll(addOverlays);
        states.getTeamOverlays().put(teamId, newSet);

        if (!activated) return;
        rmvOverlays.forEach(o -> removeOverlay(o.polygon()));
        addOverlays.forEach(o -> showOverlay(o.polygon()));

    }

    private void replaceChunk(ClaimedChunk data) {
        states.getChunkData().remove(data.chunkDimPos());
        states.getChunkData().put(data.chunkDimPos(), data);
        if (!ClaimingMode.INSTANCE.isActivated()) return;
        showForceLoaded(data.chunkDimPos(), false);
        showForceLoaded(data.chunkDimPos(), true);
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!states.getChunkData().containsKey(chunkDimPos)) return;
        final var data = states.getChunkData().get(chunkDimPos);

        if (show && data.forceLoaded() && !states.getForceLoadedOverlays().containsKey(chunkDimPos)) {
            final var claimedOverlay = ClaimingMode.INSTANCE.forceLoadedPolygon(chunkDimPos);
            showOverlay(claimedOverlay);
            states.getForceLoadedOverlays().put(chunkDimPos, claimedOverlay);
        } else if (!show && states.getForceLoadedOverlays().containsKey(chunkDimPos)) {
            removeOverlay(states.getForceLoadedOverlays().get(chunkDimPos));
            states.getForceLoadedOverlays().remove(chunkDimPos);
        }
    }

    private void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        var team = event.getTeam();
        var clientTeam = ClientTeamManagerImpl.getInstance().getTeam(team.getTeamId()).orElse(null);
        if (clientTeam == null) return;

        var oldName = event.getOldProperties().get(TeamProperties.DISPLAY_NAME);
        var nameChanged = !Objects.equals(oldName, team.getName().getString());

        Optional.ofNullable(states.getShapeProperties().get(team.getTeamId()))
                .ifPresent(prop -> prop.setFillColor(clientTeam.getColor()));
        if (!nameChanged) return;

        Optional.ofNullable(states.getTeamOverlays().get(team.getTeamId()))
                .orElse(Collections.emptySet())
                .forEach(overlay -> {
                    overlay.polygon()
                            .setLabel(clientTeam.getDisplayName());
//                            .setTitle(clientTeam.getDisplayName()));
                    removeOverlay(overlay.polygon());
                    showOverlay(overlay.polygon());
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

        states.getTeamOverlays().values().stream()
                .flatMap(Collection::stream)
                .map(PolygonWrapper::polygon)
                .forEach(action);

        activated = !activated;
    }

    private void createPolygonsOnMappingStarted() {
        final var level = mc.level;
        if (level == null) return;
        if (!activated) return;
        createPolygon(level).forEach(this::updateOverlays);
    }

    public void onJMMapping(Event.JMMappingEvent e) {
        switch (e.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                tick = -20;
                jmMappingStarted = true;
                if (!e.firstLogin()) {
                    createPolygonsOnMappingStarted();
                    log.debug("re-add ftbchunks overlays");
                }
            }

            case MAPPING_STOPPED -> {
                jmMappingStarted = false;
                states.clearOverlays();
            }
        }

    }

    public void addToQueue(MapDimension dim, ChunkSyncInfo info, UUID teamId) {
        if (!clientConfig.getFtbChunks()) return;
        queue.offer(ClaimedChunk.create(dim, info, teamId));
        tick = tick > 0 ? 1 : -20;
    }

    @Override
    public ResourceLocation getButtonIconName() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "images/ftb.png");
    }

}