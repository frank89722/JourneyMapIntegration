package frankv.jmi.util;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Displayable;

import java.util.Collection;

public class OverlayHelper {
    private static IClientAPI jmAPI;

    public static void setJmAPI(IClientAPI api) {
        jmAPI = api;
    }

    public static void showOverlay(Displayable overlay) {
        try {
            jmAPI.show(overlay);
        } catch (Throwable t) {
            JMI.LOGGER.error(String.valueOf(t));
        }
    }

    public static void showOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::showOverlay);
    }

    public static void removeOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(o -> jmAPI.remove(o));
    }
}
