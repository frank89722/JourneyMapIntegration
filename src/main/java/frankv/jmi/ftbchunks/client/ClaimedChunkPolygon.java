package frankv.jmi.ftbchunks.client;

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
import net.minecraft.util.ResourceLocation;
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
    public static List<FTBClaimedChunkData.FTBChunkQueueData> queue = new ArrayList<>();
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
        if (!JMI.CLIENT_CONFIG.getFtbChunks() || !JMI.COMMON_CONFIG.getFTBChunks()) return;
        if (mc.player == null) return;

        for (int i = 0; i<60; i++) {
            if (queue == null || queue.isEmpty()) return;
            if (!mc.player.clientLevel.dimension().equals(queue.get(0).chunkData.chunkDimPos.dimension)) {
                queue.remove(0);
                continue;
            }

            if (queue.get(0).replace) {
                replacePolygon(queue.get(0).chunkData);
                queue.remove(0);
                continue;
            }

            if (queue.get(0).isAdd) {
                addPolygon(queue.get(0).chunkData);
            } else {
                removePolygon(queue.get(0).chunkData);
            }
            queue.remove(0);

        }
    }

    private void addPolygon(FTBClaimedChunkData data) {
        final ChunkDimPos pos = data.chunkDimPos;

        try {
            if(!chunkOverlays.containsKey(pos)) {
                final PolygonOverlay overlay = createClaimedChunkOverlay(queue.get(0).chunkData);
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
        FTBClaimedChunkData chunkData = ClaimedChunkPolygon.chunkData.get(chunkDimPos);
        String teamName = chunkData.teamName;

        if (show && chunkData.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            PolygonOverlay claimedOverlay = ClaimingMode.forceLoadedPolygon(chunkDimPos);
            if (JMIOverlayHelper.createPolygon(claimedOverlay)) forceLoadedOverlays.put(chunkDimPos, claimedOverlay);

            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");

        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);

            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    public static void addToQueue(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace, boolean forceLoaded) {
        if (!JMI.CLIENT_CONFIG.getFtbChunks() || !JMI.COMMON_CONFIG.getFTBChunks()) return;
        queue.add(new FTBClaimedChunkData.FTBChunkQueueData(new FTBClaimedChunkData(dim, x, z, teamName, teamColor, forceLoaded), isAdd, replace));
    }
}