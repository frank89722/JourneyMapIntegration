package me.frankv.jmi.util;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.Displayable;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class for managing JourneyMap overlays.
 * <p>
 * This class provides methods for showing and removing overlays on JourneyMap's map.
 * It handles the case where JourneyMap mapping hasn't started yet by queuing operations
 * to be performed when mapping starts.
 */
@Slf4j
public class OverlayHelper {
    /**
     * Queue of operations to perform when JourneyMap mapping starts.
     */
    private static final List<Runnable> waitingQueue = new LinkedList<>();

    /**
     * The JourneyMap client API.
     */
    @Setter
    private static IClientAPI jmAPI;

    /**
     * Indicates whether JourneyMap mapping has started.
     */
    private static boolean jmMappingStarted = false;

    /**
     * Shows an overlay on the map.
     * <p>
     * If JourneyMap mapping hasn't started yet, the operation is queued to be performed
     * when mapping starts.
     *
     * @param overlay The overlay to show
     */
    public static void showOverlay(Displayable overlay) {
        if (!jmMappingStarted) {
            waitingQueue.add(() -> safeShowOverlay(overlay));
        } else {
            safeShowOverlay(overlay);
        }
    }

    /**
     * Removes an overlay from the map.
     * <p>
     * If JourneyMap mapping hasn't started yet, the operation is queued to be performed
     * when mapping starts.
     *
     * @param overlay The overlay to remove
     */
    public static void removeOverlay(Displayable overlay) {
        if (!jmMappingStarted) {
            waitingQueue.add(() -> safeRemoveOverlay(overlay));
        } else {
            safeRemoveOverlay(overlay);
        }
    }

    /**
     * Shows multiple overlays on the map.
     *
     * @param overlays The overlays to show
     */
    public static void showOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::showOverlay);
    }

    /**
     * Removes multiple overlays from the map.
     *
     * @param overlays The overlays to remove
     */
    public static void removeOverlays(Collection<? extends Displayable> overlays) {
        overlays.forEach(OverlayHelper::removeOverlay);
    }

    /**
     * Safely shows an overlay on the map, catching any exceptions.
     *
     * @param overlay The overlay to show
     */
    private static void safeShowOverlay(Displayable overlay) {
        try {
            jmAPI.show(overlay);
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    /**
     * Safely removes an overlay from the map, catching any exceptions.
     *
     * @param overlay The overlay to remove
     */
    private static void safeRemoveOverlay(Displayable overlay) {
        try {
            jmAPI.remove(overlay);
        } catch (Throwable t) {
            log.error(String.valueOf(t));
        }
    }

    /**
     * Handles JourneyMap mapping events.
     * <p>
     * This method updates the jmMappingStarted flag and processes the waiting queue
     * when mapping starts.
     *
     * @param event The JourneyMap mapping event
     */
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

    /**
     * Gets a JourneyMap icon resource location.
     *
     * @param string The name of the icon
     * @return The resource location for the icon
     */
    public static ResourceLocation getIcon(String string) {
        return ResourceLocation.fromNamespaceAndPath("journeymap", "theme/flat/icon/" + string + ".png");
    }
}
