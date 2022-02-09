package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import dev.ftb.mods.ftbteams.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.event.TeamEvent;
import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static frankv.jmi.JMIOverlayHelper.*;
import static frankv.jmi.JMIOverlayHelper.removePolygons;

public class ClaimedChunkPolygon {
    private IClientAPI jmAPI;
    private static final Minecraft mc = Minecraft.getInstance();

    //public static HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    public static Map<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    public static Map<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    public static Map<UUID, Set<ComparablePolygon>> chunkOverlays = new HashMap<>();
    public static Set<FTBClaimedChunkData> queue = Collections.synchronizedSet(new HashSet<>());

    private int tick = 1;
    private boolean dirty;

    public ClaimedChunkPolygon(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
    }

    public static String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        var pos = new ChunkDimPos(mc.player.level.dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!chunkData.containsKey(pos)) return "Wilderness";
        return chunkData.get(pos).team.getDisplayName();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (mc.level == null) return;
        if (!JMI.CLIENT_CONFIG.getFtbChunks()) return;
        if (tick % 5 != 0) {
            tick++;
            return;
        }

        for (var data : queue) {
            var playerDim = mc.level.dimension();

            if (data.team == null) {
                if (data.chunkDimPos.dimension.equals(playerDim)) dirty = true;
                chunkData.remove(data.chunkDimPos);
            } else if (!shouldReplace(data)) {
                if (data.chunkDimPos.dimension.equals(playerDim)) dirty = true;
                chunkData.put(data.chunkDimPos, data);
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

    public void createPolygonsOnMappingStarted() {
        var level = mc.level;
        if (level == null) return;

        //createPolygon(level);
    }

    private Map<UUID, Set<ComparablePolygon>> createPolygon(Level level) {
        var pos = new HashMap<UUID, Set<ChunkPos>>();
        var overlays = new HashMap<UUID, Set<ComparablePolygon>>();

        for (var data : chunkData.values()) {
            if (!data.chunkDimPos.dimension.equals(level.dimension())) continue;
            var teamId = data.teamId;

            if (!pos.containsKey(teamId)) {
                pos.put(teamId, new HashSet<>());
                overlays.put(teamId, new HashSet<>());
            }
            pos.get(teamId).add(data.chunkDimPos.getChunkPos());
        }

        for (var teamId : pos.keySet()) {
            var polygons = PolygonHelper.createChunksPolygon(pos.get(teamId), 10);
            var team = ClientTeamManager.INSTANCE.getTeam(teamId);

            var shapeProps = new ShapeProperties()
                    .setStrokeWidth(0f).setStrokeColor(team.getColor())
                    .setFillColor(team.getColor()).setFillOpacity((float) JMI.CLIENT_CONFIG.getClaimedChunkOverlayOpacity());

            var textProps = new TextProperties()
                    .setBackgroundColor(team.getColor()<<2)
                    .setOpacity(1f)
                    .setFontShadow(true);

            for (var polygon : polygons) {
                var overlay = new PolygonOverlay(JMI.MODID, "claimed_" + UUID.randomUUID(), level.dimension(), shapeProps, polygon);

                overlay.setOverlayGroupName("Claimed Chunks")
                        .setTitle(team.getDisplayName())
                        .setTextProperties(textProps);

                overlays.get(teamId).add(new ComparablePolygon(overlay));
            }

            //System.out.println(overlays.get(teamId).size());
        }

        return overlays;
    }

    private void replaceChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        chunkData.remove(data.chunkDimPos);
        chunkData.put(data.chunkDimPos, data);
        if (!ClaimingMode.activated) return;

        showForceLoaded(data.chunkDimPos, false);
        showForceLoaded(data.chunkDimPos, true);
    }

    public void showForceLoadedByArea(boolean show) {
        var level = mc.level;
        if (level == null) return;

        if (!show) {
//            for (var pos : forceLoadedOverlays.keySet()) {
//                chunkOverlays.get(pos).setTitle(chunkData.get(pos).team.getDisplayName());
//            }

            removePolygons(forceLoadedOverlays.values());
            forceLoadedOverlays.clear();
            return;
        }

        for (var p : ClaimingMode.area) {
            var chunkDimPos = new ChunkDimPos(level.dimension(), p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        }
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!chunkData.containsKey(chunkDimPos)) return;
        var data = chunkData.get(chunkDimPos);
        var teamName = data.team.getDisplayName();

        if (show && data.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            var claimedOverlay = ClaimingMode.forceLoadedPolygon(chunkDimPos);
            if (showOverlay(claimedOverlay)) forceLoadedOverlays.put(chunkDimPos, claimedOverlay);

//            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");

        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);

//            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    public void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        var teamId = event.getTeam().getId();
        var dim = mc.level.dimension();

        for (var data : new HashSet<>(chunkData.values())) {
            if (!data.teamId.equals(teamId)) continue;

            data.updateOverlayProps();
            replaceChunk(data, dim);
        }
    }

    public static void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!JMI.ftbchunks) return;
        queue.add(new FTBClaimedChunkData(dim, chunk, teamId));
    }

    private static boolean shouldReplace(FTBClaimedChunkData data) {
        if (data.team == null) return false;

        var that = chunkData.get(data.chunkDimPos);
        if (that == null) return false;
        return !data.equals(that);
    }

    class ComparablePolygon {
        final PolygonOverlay polygon;

        public ComparablePolygon(PolygonOverlay polygon) {
            this.polygon = polygon;
        }

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