package me.frankv.jmi.api.event;

import journeymap.client.api.display.ThemeButtonDisplay;
import journeymap.client.api.event.ClientEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public interface Event {
    record ClientTick() implements Event {}
    record AddButtonDisplay(ThemeButtonDisplay themeButtonDisplay) implements Event {}
    record ScreenClose(Screen screen) implements Event {}
    record MouseRelease(int button) implements Event {}
    record ScreenDraw(Screen screen, GuiGraphics guiGraphics) implements Event {}
    record PlayerInteract(BlockPos pos, ItemStack itemStack) implements Event {}
    record ResetDataEvent() implements Event {}
    record JMClientEvent(ClientEvent clientEvent, boolean firstLogin) implements Event {}
}
