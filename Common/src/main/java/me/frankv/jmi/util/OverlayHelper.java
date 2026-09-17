package me.frankv.jmi.util;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.Displayable;
import journeymap.api.v2.client.event.MappingEvent;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
        if (event.mappingEvent().getStage() == MappingEvent.Stage.MAPPING_STARTED) {
            jmMappingStarted = true;
            waitingQueue.forEach(Runnable::run);
        } else {
            jmMappingStarted = false;
        }
        waitingQueue.clear();
    }

    public static ResourceLocation getIcon(String name) {
        return new ResourceLocation("journeymap", "theme/flat/icon/" + name + ".png");
    }

}
