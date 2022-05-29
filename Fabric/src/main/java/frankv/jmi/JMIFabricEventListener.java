package frankv.jmi;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.ftbchunks.client.ClaimingMode;
import frankv.jmi.ftbchunks.client.ClaimingModeHandler;
import frankv.jmi.ftbchunks.client.GeneralDataOverlay;
import frankv.jmi.waystones.client.WaystoneMarker;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;
import journeymap.client.api.model.IFullscreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import static frankv.jmi.ftbchunks.client.ClaimingMode.activated;
import static frankv.jmi.ftbchunks.client.ClaimingMode.buttonControl;

public class JMIFabricEventListener {
    public static boolean firstLogin;
    private static boolean haveDim;

    public JMIFabricEventListener() {
        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(this::onAddonButtonDisplay);
        ScreenEvents.AFTER_INIT.register(this::onGuiScreen);
    }

    public void onClientTick(Minecraft mc) {
        var level = mc.level;

        if (level == null) {
            if (haveDim) {
                haveDim = false;
                ClaimedChunkPolygon.chunkData.clear();
                WaystoneMarker.waystones.clear();
                JMI.LOGGER.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) firstLogin = haveDim = true;

        if (JMI.ftbchunks) {
            ClaimedChunkPolygon.onClientTick();
        }
    }

    public void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        if (JMI.ftbchunks) {
            var buttonDisplay = event.getThemeButtonDisplay();
            buttonDisplay.addThemeToggleButton("FTBChunks Claiming Mode", "FTBChunks Claiming Mode", "grid", activated, b -> buttonControl(b));
        }
    }

    private void onGuiScreen(Minecraft minecraft, Screen screen, int i, int i1) {
        if (JMI.ftbchunks) {
            ScreenEvents.remove(screen).register(event -> ClaimingMode.onGuiScreen(screen));

            if (screen instanceof IFullscreen) {
                ScreenMouseEvents.afterMouseRelease(screen).register((screenE, mouseX, mouseY, button) -> ClaimingModeHandler.onMouseReleased(button));
                ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) -> GeneralDataOverlay.onScreenDraw(screen, stack));
            }

        }
    }
}
