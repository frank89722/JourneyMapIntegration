package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import frankv.jmi.JMI;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

import static frankv.jmi.JMIOverlayHelper.removePolygons;

public class ClaimedChunkPolygon {
    private IClientAPI jmAPI;
    private static final Minecraft mc = Minecraft.getInstance();

    public static HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    public static HashMap<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    public static HashMap<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    public static List<FTBClaimedChunkData> queue = new LinkedList<>();


    public ClaimedChunkPolygon(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    public static String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        var pos = new ChunkDimPos(mc.player.level.dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
        return chunkOverlays.get(pos).getTitle();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!JMI.CLIENT_CONFIG.getFtbChunks()) return;
        if (mc.level == null) return;

        for (var i = 0; i<60; ++i) {
            if (queue == null || queue.isEmpty()) return;

            var playerDim = mc.level.dimension();
            var data = queue.get(0);

            if (data.team == null) removeChunk(data, playerDim);
            else if (shouldReplace(data)) replaceChunk(data, playerDim);
            else addChunk(data, playerDim);

            queue.remove(0);
        }
    }

    public void createPolygonsOnMappingStarted() {
        var level = mc.level;

        if (level == null) return;

        for (var data : chunkData.values()) {
            if (data.chunkDimPos.dimension.equals(level.dimension())) createPolygon(data);
        }
    }

    private void addChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        var pos = data.chunkDimPos;

        if (chunkOverlays.containsKey(pos)) return;

        chunkData.put(pos, data);
        if (pos.dimension.equals(dim)) createPolygon(data);
    }

    private void removeChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        var pos = data.chunkDimPos;

        if (!chunkOverlays.containsKey(pos)) return;

        chunkData.remove(pos);

        if (!pos.dimension.equals(dim)) return;

        try {
            jmAPI.remove(chunkOverlays.get(pos));
            chunkOverlays.remove(pos);
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void replaceChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        removeChunk(data, dim);
        addChunk(data, dim);
        if (ClaimingMode.activated) {
            showForceLoaded(data.chunkDimPos, false);
            showForceLoaded(data.chunkDimPos, true);
        }
    }

    private void createPolygon(FTBClaimedChunkData data) {
        var player = mc.player;
        if (player == null) return;

        var overlay = createClaimedChunkOverlay(data, player);

        try {
            jmAPI.show(overlay);
            chunkOverlays.put(data.chunkDimPos, overlay);
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private static PolygonOverlay createClaimedChunkOverlay(FTBClaimedChunkData data, Player player) {
        var displayId = "claimed_" + data.chunkDimPos.x + ',' + data.chunkDimPos.z;
        var shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(data.teamColor).setFillOpacity((float) JMI.CLIENT_CONFIG.getClaimedChunkOverlayOpacity());

        var textProps = new TextProperties()
                .setColor(data.teamColor)
                .setOpacity(1f)
                .setFontShadow(true);

        var polygon = PolygonHelper.createChunkPolygon(data.chunkDimPos.x, 1, data.chunkDimPos.z);

        var ClaimedChunkOverlay = new PolygonOverlay(JMI.MODID, displayId, player.level.dimension(), shapeProps, polygon);

        ClaimedChunkOverlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(data.teamName)
                .setTextProperties(textProps);

        return ClaimedChunkOverlay;
    }

    public void showForceLoadedByArea(boolean show) {
        var level = mc.level;
        if (level == null) return;

        if (!show) {
            for (var pos : forceLoadedOverlays.keySet()) {
                chunkOverlays.get(pos).setTitle(chunkData.get(pos).teamName);
            }

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
        var teamName = data.teamName;

        if (show && data.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            var claimedOverlay = ClaimingMode.forceLoadedPolygon(chunkDimPos);
            if (JMIOverlayHelper.createPolygon(claimedOverlay)) forceLoadedOverlays.put(chunkDimPos, claimedOverlay);

            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");

        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);

            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    public static void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!JMI.CLIENT_CONFIG.getFtbChunks()) return;
        queue.add(new FTBClaimedChunkData(dim, chunk, teamId));
    }

    private static boolean shouldReplace(FTBClaimedChunkData data) {
        if (data.team == null) return false;

        var that = chunkData.get(data.chunkDimPos);
        if (that == null) return false;
        return !data.equals(that);
    }
}