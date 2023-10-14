package me.frankv.jmi.util;

import lombok.extern.slf4j.Slf4j;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Displayable;

import java.util.Collection;

@Slf4j
public class OverlayHelper {
    private static IClientAPI jmAPI;

    public static void setJmAPI(IClientAPI api) {
        jmAPI = api;
    }

    public static void showOverlay(Displayable overlay) {
        try {
            jmAPI.show(overlay);
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    public static void showOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::showOverlay);
    }

    public static void removeOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(o -> jmAPI.remove(o));
    }
}
