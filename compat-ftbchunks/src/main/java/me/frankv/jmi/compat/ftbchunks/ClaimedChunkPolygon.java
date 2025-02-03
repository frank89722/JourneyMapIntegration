package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.fullscreen.IThemeButton;
import journeymap.api.v2.client.model.MapPolygon;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.model.TextProperties;
import journeymap.api.v2.client.util.PolygonHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

import static me.frankv.jmi.util.OverlayHelper.removeOverlays;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;

@Slf4j
public enum ClaimedChunkPolygon implements ToggleableOverlay {
    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();
    //    private final HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    @Getter
    private final HashMap<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    @Getter
    private final HashMap<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    private final Queue<FTBClaimedChunkData> queue = new LinkedList<>();
    @Getter
    private final String buttonLabel = "FTBChunks Overlay";
    @Getter
    private final int order = 1;
    @Getter
    private Map<UUID, Set<ComparablePolygon>> chunkOverlays = new HashMap<>();
    private IClientAPI jmAPI = null;
    private ClientConfig clientConfig;
    @Getter
    private boolean activated = true;
    private Boolean shouldToggleAfterOff = false;

    private int tick = 1;
    private boolean dirty;

    public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
        this.jmAPI = jmAPI;
        this.clientConfig = clientConfig;

        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
        disableFTBChunksStuff();
    }

    private void updateOverlays(Map<UUID, Set<ComparablePolygon>> overlays) {
        var newOverlays = new HashMap<>(overlays);
        var oldOverlays = new HashMap<>(chunkOverlays);
        var addOverlays = new HashMap<>(newOverlays);
        var rmvOverlays = new HashMap<>(oldOverlays);
        rmvOverlays.entrySet().removeAll(newOverlays.entrySet());
        addOverlays.entrySet().removeAll(oldOverlays.entrySet());
        rmvOverlays.values().forEach(team -> team.forEach(o -> jmAPI.remove(o.polygon)));
        addOverlays.values().forEach(team -> team.forEach(o -> showOverlay(o.polygon)));
        chunkOverlays = (Map<UUID, Set<ComparablePolygon>>) newOverlays.clone();
    }

    private Map<UUID, Set<ComparablePolygon>> createPolygon(Level level) {
        var pos = new HashMap<UUID, Set<ChunkPos>>();
        var overlays = new HashMap<UUID, Set<ComparablePolygon>>();

        for (var data : chunkData.values()) {
            if (!data.getChunkDimPos().dimension().equals(level.dimension())) continue;
            var teamId = data.getTeamId();

            if (!pos.containsKey(teamId)) {
                pos.put(teamId, new HashSet<>());
                overlays.put(teamId, new HashSet<>());
            }
            pos.get(teamId).add(data.getChunkDimPos().chunkPos());
        }

        for (var teamId : pos.keySet()) {
            var polygons = PolygonHelper.createChunksPolygon(pos.get(teamId), 10);
            var team = ClientTeamManagerImpl.getInstance().getTeam(teamId).orElse(null);
            if (team == null) continue;

            var shapeProps = new ShapeProperties()
                    .setStrokeWidth(0f).setStrokeColor(team.getColor())
                    .setStrokeOpacity(1f)
                    .setFillColor(team.getColor()).setFillOpacity(clientConfig.getClaimedChunkOverlayOpacity().floatValue());

            var textProps = new TextProperties()
                    .setBackgroundColor(team.getColor() << 2)
                    .setOpacity(1f)
                    .setFontShadow(true);

            for (var polygon : polygons) {

                var overlay = new PolygonOverlay(Constants.MOD_ID, level.dimension(), shapeProps, polygon);

                overlay.setOverlayGroupName("Claimed Chunks")
                        .setLabel(team.getDisplayName())
                        .setTitle(team.getDisplayName())
//                        .setOverlayListener(new TestLis())
                        .setTextProperties(textProps);

                overlays.get(teamId).add(new ComparablePolygon(overlay));
            }

            //System.out.println(overlays.get(teamId).size());
        }

        return overlays;
    }

    private String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        final var pos = new ChunkDimPos(mc.player.level().dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
//        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
//        return chunkOverlays.get(pos).getTitle();
        if (!chunkData.containsKey(pos)) return "Wilderness";
        return chunkData.get(pos).getTeam().getDisplayName();
    }

    private void createPolygonsOnMappingStarted() {
        final var level = mc.level;

        if (level == null) return;

//        chunkData.values().forEach(data -> {
//            if (!data.getChunkDimPos().dimension().equals(level.dimension())) return;
//            chunkOverlays.put(data.getChunkDimPos(), data.getOverlay());
//            if (!activated) return;
//            showOverlay(data.getOverlay());
//        });
    }

//    private void addChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
//        final var pos = data.getChunkDimPos();
//
//        if (chunkOverlays.containsKey(pos)) return;
//
//        chunkData.put(pos, data);
//        if (!pos.dimension().equals(dim)) return;
//
//        chunkOverlays.put(data.getChunkDimPos(), data.getOverlay());
//        if (activated) showOverlay(data.getOverlay());
//
//    }

//    private void removeChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
//        final var pos = data.getChunkDimPos();
//        if (!chunkOverlays.containsKey(pos)) return;
//        chunkData.remove(pos);
//        if (!pos.dimension().equals(dim)) return;
//        try {
//            jmAPI.remove(chunkOverlays.get(pos));
//            chunkOverlays.remove(pos);
//        } catch (Throwable t) {
//            log.error(t.getMessage(), t);
//        }
//    }

    private void replaceChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
//        removeChunk(data, dim);
//        addChunk(data, dim);
//        if (ClaimingMode.INSTANCE.isActivated()) {
//            showForceLoaded(data.getChunkDimPos(), false);
//            showForceLoaded(data.getChunkDimPos(), true);
//        }
        chunkData.remove(data.getChunkDimPos());
        chunkData.put(data.getChunkDimPos(), data);
        if (!ClaimingMode.INSTANCE.isActivated()) return;
        showForceLoaded(data.getChunkDimPos(), false);
        showForceLoaded(data.getChunkDimPos(), true);
    }

    public void showForceLoadedByArea(boolean show) {
        final var level = mc.level;
        if (level == null) return;

        if (!show) {
//            forceLoadedOverlays.keySet()
//                    .forEach(pos -> chunkOverlays.get(pos).setTitle(chunkData.get(pos).getTeam().getDisplayName()));

            removeOverlays(forceLoadedOverlays.values());
            forceLoadedOverlays.clear();
            return;
        }

        ClaimingMode.INSTANCE.getArea().forEach(p -> {
            final var chunkDimPos = new ChunkDimPos(level.dimension(), p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        });
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!chunkData.containsKey(chunkDimPos)) return;
        final var data = chunkData.get(chunkDimPos);
        final var teamName = data.getTeam().getDisplayName();

        if (show && data.isForceLoaded() && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            final var claimedOverlay = ClaimingMode.INSTANCE.forceLoadedPolygon(chunkDimPos);
            showOverlay(claimedOverlay);
            forceLoadedOverlays.put(chunkDimPos, claimedOverlay);
//            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");
        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);
//            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    private boolean shouldReplace(FTBClaimedChunkData data) {
        if (data.getTeam() == null) return false;

        final var that = chunkData.get(data.getChunkDimPos());
        if (that == null) return false;
        return !data.equals(that);
    }

    private void disableFTBChunksStuff() {
        if (!clientConfig.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }

    private void clearOverlays() {
        chunkOverlays.clear();
        forceLoadedOverlays.clear();
    }

    private void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        final var teamId = event.getTeam().getId();
        final var dim = mc.level.dimension();

        new HashSet<>(chunkData.values()).forEach(data -> {
            if (!data.getTeamId().equals(teamId)) return;

            data.updateOverlayProps();
            replaceChunk(data, dim);
        });
    }

    public void onClientTick() {
        if (!clientConfig.getFtbChunks()) return;
        if (mc.level == null) return;

        if (tick % 5 != 0) {
            tick++;
            return;
        }

        for (var data : queue) {
            var playerDim = mc.level.dimension();

            if (data.getTeam() == null) {
                if (data.getChunkDimPos().dimension().equals(playerDim)) dirty = true;
                chunkData.remove(data.getChunkDimPos());
            } else if (!shouldReplace(data)) {
                if (data.getChunkDimPos().dimension().equals(playerDim)) dirty = true;
                chunkData.put(data.getChunkDimPos(), data);
            } else {
                replaceChunk(data, playerDim);
            }
        }

        if (dirty) {
            var overlays = createPolygon(mc.level);
            updateOverlays(overlays);
            dirty = false;
        }
        queue.clear();
        tick = 1;

//        for (var i = 0; i < 200; ++i) {
//            if (queue.isEmpty()) return;
//
//            final var playerDim = mc.level.dimension();
//            final var data = queue.poll();
//
//            if (data.getTeam() == null) removeChunk(data, playerDim);
//            else if (shouldReplace(data)) replaceChunk(data, playerDim);
//            else addChunk(data, playerDim);
//        }
    }

    void onClaiming(boolean off) {
        if (!off && activated) return;
        if (!off) {
            toggleOverlay();
            shouldToggleAfterOff = true;
        } else if (shouldToggleAfterOff) {
            toggleOverlay();
            shouldToggleAfterOff = false;
        }
    }

    private void toggleOverlay() {
//        if (activated) {
//            OverlayHelper.removeOverlays(chunkOverlays.values());
//        } else {
//            OverlayHelper.showOverlays(chunkOverlays.values());
//        }

//        activated = !activated;
    }

    @Override
    public void onToggle(IThemeButton button) {
        if (ClaimingMode.INSTANCE.isActivated()) return;
        toggleOverlay();
        button.setToggled(activated);
    }

    public void onJMMapping(Event.JMMappingEvent e) {
        switch (e.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                if (!e.firstLogin()) {
                    createPolygonsOnMappingStarted();
                    log.debug("re-add ftbchunks overlays");
                }
            }

            case MAPPING_STOPPED -> clearOverlays();
        }

    }

    public void onJMInfoSlotRegistryEvent(Event.JMInfoSlotRegistryEvent e) {
        e.infoSlotRegistryEvent().register(Constants.MOD_ID,
                "jmi.infoslot.ftbchunks",
                1000L,
                this::getPolygonTitleByPlayerPos);
    }

    public void addToQueue(MapDimension dim, ChunkSyncInfo info, UUID teamId) {
        if (!clientConfig.getFtbChunks()) return;
        queue.offer(new FTBClaimedChunkData(dim, info, teamId));
    }

    @Override
    public ResourceLocation getButtonIconName() {
        return ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "images/ftb.png");
    }

    public record ComparablePolygon(PolygonOverlay polygon) {
        private Set<Set<BlockPos>> makePosSet(List<MapPolygon> mapPolygons) {
            var result = new HashSet<Set<BlockPos>>();
            mapPolygons.forEach(o -> result.add((new HashSet<>(o.getPoints()))));
            return result;
        }

        private boolean compareHoles(List<MapPolygon> that) {
            if (that == null) return false;
            var holesSet = makePosSet(polygon.getHoles());
            var thatSet = makePosSet(that);
            return holesSet.equals(thatSet);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ComparablePolygon that = (ComparablePolygon) o;
            return Objects.equals(polygon.getOuterArea().getPoints(), that.polygon.getOuterArea().getPoints()) && compareHoles(that.polygon.getHoles());
        }

        @Override
        public int hashCode() {
            return Objects.hash(polygon.getOuterArea().getPoints(), makePosSet(polygon.getHoles()));
        }
    }

}