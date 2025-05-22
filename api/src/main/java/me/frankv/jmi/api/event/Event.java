package me.frankv.jmi.api.event;

import journeymap.api.v2.client.event.FullscreenMapEvent;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.client.event.RegistryEvent;
import journeymap.api.v2.client.fullscreen.ThemeButtonDisplay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

/**
 * Interface defining the event types used in the JMI event system.
 * <p>
 * This interface contains nested record classes that represent different types of events
 * that can be dispatched through the {@link JMIEventBus}. These events include both general
 * game events and JourneyMap-specific events.
 * <p>
 * Components can subscribe to specific event types using the {@link JMIEventBus#subscribe} method.
 */
public interface Event {
    /**
     * Event fired on each client tick.
     */
    record ClientTick() implements Event {
    }

    /**
     * Event fired when JourneyMap's button display is being added.
     * <p>
     * This event allows compatibility modules to add their own buttons to JourneyMap's UI.
     *
     * @param themeButtonDisplay The theme button display to add buttons to
     */
    record AddButtonDisplay(ThemeButtonDisplay themeButtonDisplay) implements Event {
    }

    /**
     * Event fired when a screen is closed.
     *
     * @param screen The screen that was closed
     */
    record ScreenClose(Screen screen) implements Event {
    }

    /**
     * Event fired when a mouse button is released.
     *
     * @param button The button that was released
     */
    record MouseRelease(int button) implements Event {
    }

    /**
     * Event fired when a screen is drawn.
     *
     * @param screen The screen being drawn
     * @param guiGraphics The GUI graphics context
     */
    record ScreenDraw(Screen screen, GuiGraphics guiGraphics) implements Event {
    }

    /**
     * Event fired when the player interacts with a block.
     *
     * @param pos The position of the block
     * @param itemStack The item stack used for the interaction
     */
    record PlayerInteract(BlockPos pos, ItemStack itemStack) implements Event {
    }

    /**
     * Event fired when data should be reset.
     */
    record ResetDataEvent() implements Event {
    }

    /**
     * Event fired when JourneyMap mapping events occur.
     *
     * @param mappingEvent The JourneyMap mapping event
     * @param firstLogin Whether this is the first login
     */
    record JMMappingEvent(MappingEvent mappingEvent, boolean firstLogin) implements Event {
    }

    /**
     * Event fired when JourneyMap info slot registry events occur.
     *
     * @param infoSlotRegistryEvent The JourneyMap info slot registry event
     */
    record JMInfoSlotRegistryEvent(RegistryEvent.InfoSlotRegistryEvent infoSlotRegistryEvent) implements Event {
    }

    /**
     * Event fired when the mouse is dragged on JourneyMap's fullscreen map.
     *
     * @param mouseDraggedEvent The JourneyMap mouse dragged event
     */
    record JMMouseDraggedEvent(FullscreenMapEvent.MouseDraggedEvent mouseDraggedEvent) implements Event {
    }

    /**
     * Event fired when the mouse is moved on JourneyMap's fullscreen map.
     *
     * @param mouseMoveEvent The JourneyMap mouse move event
     */
    record JMMouseMoveEvent(FullscreenMapEvent.MouseMoveEvent mouseMoveEvent) implements Event {
    }

    /**
     * Event fired when the mouse is clicked on JourneyMap's fullscreen map.
     *
     * @param clickEvent The JourneyMap click event
     */
    record JMClickEvent(FullscreenMapEvent.ClickEvent clickEvent) implements Event {
    }
}
