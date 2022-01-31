package frankv.jmi;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.waystones.client.WaystoneMarker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class JMIForgeEventListener {
    private static final Minecraft mc = Minecraft.getInstance();
    public static boolean firstLogin;
    private static boolean haveDim;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        var level = mc.level;

        if (level == null) {
            if (haveDim) {
                haveDim = false;
                ClaimedChunkPolygon.chunkData.clear();
                WaystoneMarker.waystones.clear();
                JMI.LOGGER.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) firstLogin = haveDim = true;
    }
}
