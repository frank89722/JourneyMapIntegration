package me.frankv.jmi.util;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.Displayable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Slf4j
public class OverlayHelper {
    @Setter
    private static IClientAPI jmAPI;

    private static List<Displayable> waitingQueue = new LinkedList<>();

    private static boolean jmMappingStarted = false;


    public static void showOverlay(Displayable overlay) {
        try {
            if (jmMappingStarted) {
                jmAPI.show(overlay);
            } else {
                waitingQueue.add(overlay);
            }
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    public static void showOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::showOverlay);
    }

    public static void removeOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(jmAPI::remove);
    }

    public static void onJMEvent(Event.JMMappingEvent event) {
        switch (event.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                jmMappingStarted = true;
                waitingQueue.forEach(OverlayHelper::showOverlay);
            }
            case MAPPING_STOPPED -> {
                jmMappingStarted = false;
                waitingQueue.clear();
            }
        }
    }

}
