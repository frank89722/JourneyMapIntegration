package me.frankv.jmi.util;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.Displayable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;
import net.minecraft.resources.ResourceLocation;

import java.util.*;
import java.util.stream.IntStream;

@Slf4j
public class OverlayHelper {
    private static final List<Runnable> waitingQueue = new LinkedList<>();
    @Setter
    private static IClientAPI jmAPI;
    private static boolean jmMappingStarted = false;


    public static void showOverlay(Displayable overlay) {
        if (!jmMappingStarted) {
            waitingQueue.add(() -> safeShowOverlay(overlay));
        } else {
            safeShowOverlay(overlay);
        }
    }

    public static void removeOverlay(Displayable overlay) {
        if (!jmMappingStarted) {
            waitingQueue.add(() -> safeRemoveOverlay(overlay));
        } else {
            safeRemoveOverlay(overlay);
        }
    }

    public static void showOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::showOverlay);
    }

    public static void removeOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::removeOverlay);
    }

    private static void safeShowOverlay(Displayable overlay) {
        try {
            jmAPI.show(overlay);
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    private static void safeRemoveOverlay(Displayable overlay) {
        try {
            jmAPI.remove(overlay);
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    public static void onJMMapping(Event.JMMappingEvent event) {
        switch (event.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                jmMappingStarted = true;
                waitingQueue.forEach(Runnable::run);
            }
            case MAPPING_STOPPED -> jmMappingStarted = false;
            default -> {
                jmMappingStarted = false;
                throw new IllegalStateException("Unexpected value: " + event.mappingEvent().getStage());
            }
        }
        waitingQueue.clear();
    }

    public static ResourceLocation getIcon(String string) {
        return ResourceLocation.fromNamespaceAndPath("journeymap", "theme/flat/icon/" + string + ".png");
    }

}
