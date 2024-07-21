package me.frankv.jmi.api.event;

import journeymap.api.v2.client.event.FullscreenMapEvent;
import journeymap.api.v2.client.event.MappingEvent;
import journeymap.api.v2.client.event.RegistryEvent;
import journeymap.api.v2.client.fullscreen.ThemeButtonDisplay;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface Event {
    record ClientTick() implements Event {
    }

    record AddButtonDisplay(ThemeButtonDisplay themeButtonDisplay) implements Event {
    }

    record ScreenClose(Screen screen) implements Event {
    }

    record MouseRelease(int button) implements Event {
    }

    record ScreenDraw(Screen screen, GuiGraphics guiGraphics) implements Event {
    }

    record PlayerInteract(BlockPos pos, ItemStack itemStack) implements Event {
    }

    record ResetDataEvent() implements Event {
    }

    record JMMappingEvent(MappingEvent mappingEvent, boolean firstLogin) implements Event {
    }

    record JMInfoSlotRegistryEvent(RegistryEvent.InfoSlotRegistryEvent infoSlotRegistryEvent) implements Event {
    }

    record JMMouseDraggedEvent(FullscreenMapEvent.MouseDraggedEvent mouseDraggedEvent) implements Event {
    }

    record JMMouseMoveEvent(FullscreenMapEvent.MouseMoveEvent mouseMoveEvent) implements Event {
    }

    record JMClickEvent(FullscreenMapEvent.ClickEvent clickEvent) implements Event {
    }


}
