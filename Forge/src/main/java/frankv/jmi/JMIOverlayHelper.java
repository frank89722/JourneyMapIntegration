package frankv.jmi;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.PolygonOverlay;

import java.util.Collection;

public class JMIOverlayHelper {
    private static IClientAPI jmAPI;

    public static void setJmAPI(IClientAPI api) {
        jmAPI = api;
    }

    public static boolean createPolygon(PolygonOverlay overlay) {
        try {
            jmAPI.show(overlay);
        } catch (Throwable t) {
            JMI.LOGGER.error(t);
            return false;
        }
        return true;
    }

    public static void removePolygons(Collection<PolygonOverlay> overlays) {
        for (var o : overlays) jmAPI.remove(o);
    }
}
