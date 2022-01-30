package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import frankv.jmi.JMI;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ClaimedChunkPolygon {
    private IClientAPI jmAPI;
    public static HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    public static HashMap<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    public static HashMap<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    public static List<FTBClaimedChunkData> queue = new LinkedList<>();
    private static Minecraft mc = Minecraft.getInstance();

    public ClaimedChunkPolygon(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    public static String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        ChunkDimPos pos = new ChunkDimPos(mc.player.clientLevel.dimension(), mc.player.xChunk, mc.player.zChunk);
        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
        return chunkOverlays.get(pos).getTitle();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (!JMI.CLIENT_CONFIG.getFtbChunks()) return;
        if (mc.player == null) return;

        for (int i = 0; i<60; i++) {
            if (queue == null || queue.isEmpty()) return;

            if (queue.get(0).team == null) {
                removePolygon(queue.get(0));
                queue.remove(0);
                continue;
            }

            if (shouldReplace(queue.get(0))) {
                replacePolygon(queue.get(0));
                queue.remove(0);
                continue;
            }

            addPolygon(queue.get(0));
            queue.remove(0);

        }
    }

    private void addPolygon(FTBClaimedChunkData data) {
        final ChunkDimPos pos = data.chunkDimPos;

        try {
            if(!chunkOverlays.containsKey(pos)) {
                final PolygonOverlay overlay = createClaimedChunkOverlay(queue.get(0));
                chunkOverlays.put(pos, overlay);
                chunkData.put(pos, data);
                jmAPI.show(overlay);
            }
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void removePolygon(FTBClaimedChunkData data) {
        final ChunkDimPos pos = data.chunkDimPos;

        try {
            jmAPI.remove(chunkOverlays.get(pos));
            chunkOverlays.remove(pos);
            chunkData.remove(pos);
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void replacePolygon(FTBClaimedChunkData data) {
        removePolygon(data);
        addPolygon(data);
        if (ClaimingMode.activated) {
            showForceLoaded(data.chunkDimPos, false);
            showForceLoaded(data.chunkDimPos, true);
        }
    }

    private static PolygonOverlay createClaimedChunkOverlay(FTBClaimedChunkData data) {
        final ClientPlayerEntity player = mc.player;

        String displayId = "claimed_" + data.chunkDimPos.x + ',' + data.chunkDimPos.z;
        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(data.teamColor).setFillOpacity((float) JMI.CLIENT_CONFIG.getClaimedChunkOverlayOpacity());

        TextProperties textProps = new TextProperties()
                .setColor(data.teamColor)
                .setOpacity(1f)
                .setFontShadow(true);

        MapPolygon polygon = PolygonHelper.createChunkPolygon(data.chunkDimPos.x, 1, data.chunkDimPos.z);

        PolygonOverlay ClaimedChunkOverlay = new PolygonOverlay(JMI.MODID, displayId, player.clientLevel.dimension(), shapeProps, polygon);

        ClaimedChunkOverlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(data.teamName)
                .setTextProperties(textProps);

        return ClaimedChunkOverlay;
    }

    public void showForceLoadedByArea(boolean show) {
        RegistryKey<World> clientLevel = mc.player.clientLevel.dimension();

        if (!show) {
            for (ChunkDimPos pos : forceLoadedOverlays.keySet()) {
                chunkOverlays.get(pos).setTitle(chunkData.get(pos).teamName);
            }

            JMIOverlayHelper.removePolygons(forceLoadedOverlays.values());
            forceLoadedOverlays.clear();
            return;
        }

        for (ChunkPos p : ClaimingMode.area) {
            ChunkDimPos chunkDimPos = new ChunkDimPos(clientLevel, p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        }
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!chunkData.containsKey(chunkDimPos)) return;
        FTBClaimedChunkData data = ClaimedChunkPolygon.chunkData.get(chunkDimPos);
        String teamName = data.teamName;

        if (show && data.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            PolygonOverlay claimedOverlay = ClaimingMode.forceLoadedPolygon(chunkDimPos);
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

        FTBClaimedChunkData that = chunkData.get(data.chunkDimPos);
        if (that == null) return false;
        return !data.equals(that);
    }
}