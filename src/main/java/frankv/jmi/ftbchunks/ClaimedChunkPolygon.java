package frankv.jmi.ftbchunks;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.MapPolygon;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class ClaimedChunkPolygon {
    private IClientAPI jmAPI;
    private static HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays;
    private static List<FTBChunkDataBuffer> queue = new ArrayList<>();
    private static Minecraft mc = Minecraft.getInstance();


    public ClaimedChunkPolygon(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        this.chunkOverlays = new HashMap<>();
    }

    public static String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        ChunkDimPos pos = new ChunkDimPos(mc.player.clientLevel.dimension(), mc.player.xChunk, mc.player.zChunk);
        if (chunkOverlays.containsKey(pos)) {
            System.out.println(chunkOverlays.get(pos).getTitle());
            return chunkOverlays.get(pos).getTitle();
        }
        return "Widerness";
    }

    @SubscribeEvent
    public void onPlayerChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        chunkOverlays.clear();
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (queue == null || queue.isEmpty()) return;
        if (mc.player == null) return;
        if (!mc.player.clientLevel.dimension().location().equals(queue.get(0).dim)) {
            queue.remove(0);
            return;
        }

        final ChunkDimPos pos = new ChunkDimPos(mc.player.clientLevel.dimension(), queue.get(0).x, queue.get(0).z);

        if (queue.get(0).replace) {
            replacePolygon(pos);
            queue.remove(0);
            return;
        }

        if (queue.get(0).isAdd) {
            addPolygon(pos);
        } else {
            removePolygon(pos);
        }
        queue.remove(0);
    }

    private void addPolygon(ChunkDimPos pos) {
        try {
            if(!chunkOverlays.containsKey(pos)) {
                final PolygonOverlay overlay = createOverlay(queue.get(0));
                chunkOverlays.put(pos, overlay);
                jmAPI.show(overlay);
            }
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void removePolygon(ChunkDimPos pos) {
        try {
            jmAPI.remove(chunkOverlays.get(pos));
            chunkOverlays.remove(pos);
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void replacePolygon(ChunkDimPos pos) {
        removePolygon(pos);
        addPolygon(pos);
    }

    private static PolygonOverlay createOverlay(FTBChunkDataBuffer tb) {
        final ClientPlayerEntity player = Minecraft.getInstance().player;

        String displayId = "claimed_" + tb.x + ',' + tb.z;
        ShapeProperties shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(tb.teamColor).setFillOpacity(.3f);

        TextProperties textProps = new TextProperties()
                .setColor(tb.teamColor)
                .setOpacity(1f)
                .setMinZoom(2)
                .setFontShadow(true);

        MapPolygon polygon = PolygonHelper.createChunkPolygon(tb.x, 0, tb.z);

        PolygonOverlay ClaimedChunkOverlay = new PolygonOverlay(JMI.MODID, displayId, player.clientLevel.dimension(), shapeProps, polygon);

        ClaimedChunkOverlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(tb.teamName)
                .setTextProperties(textProps);

        return ClaimedChunkOverlay;
    }

    public static void addToQueue(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace) {
        queue.add(new FTBChunkDataBuffer(dim, x, z, teamName, teamColor, isAdd, replace));
    }
}