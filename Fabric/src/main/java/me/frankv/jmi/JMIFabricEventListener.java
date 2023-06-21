package me.frankv.jmi;

import me.frankv.jmi.jmoverlay.ftbchunks.ClaimingMode;
import me.frankv.jmi.jmoverlay.ftbchunks.GeneralDataOverlay;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;
import journeymap.client.api.model.IFullscreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

public class JMIFabricEventListener extends PlatformEventListener {

    public void register() {
        ClientTickEvents.START_CLIENT_TICK.register(this::onStartClientTick);
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(this::onAddonButtonDisplayEvent);
        ScreenEvents.AFTER_INIT.register(this::onGuiScreen);
    }

    private void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        onAddonButtonDisplay(event.getThemeButtonDisplay());
    }

    private void onStartClientTick(Minecraft mc) {
        onClientTick();
    }

    private void onGuiScreen(Minecraft minecraft, Screen screen, int i, int i1) {
        ScreenEvents.remove(screen).register(event -> ClaimingMode.INSTANCE.onGuiScreen(screen));

        if (!(screen instanceof IFullscreen)) return;

        ScreenMouseEvents.afterMouseRelease(screen).register((screenE, mouseX, mouseY, button) -> ClaimingMode.INSTANCE.getHandler().onMouseReleased(button));
        ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) -> GeneralDataOverlay.onScreenDraw(screen, stack));
    }
}
