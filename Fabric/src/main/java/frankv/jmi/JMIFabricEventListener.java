package frankv.jmi;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.waystones.client.WaystoneMarker;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class JMIFabricEventListener {
    public static boolean firstLogin;
    private static boolean haveDim;

    public JMIFabricEventListener() {
        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
    }

    public void onClientTick(Minecraft mc) {
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
