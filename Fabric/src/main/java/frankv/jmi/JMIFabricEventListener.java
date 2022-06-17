package frankv.jmi;

import frankv.jmi.jmoverlay.JMOverlayManager;
import frankv.jmi.jmoverlay.ftbchunks.ClaimedChunkPolygon;
import frankv.jmi.jmoverlay.ftbchunks.ClaimingMode;
import frankv.jmi.jmoverlay.ftbchunks.GeneralDataOverlay;
import frankv.jmi.jmoverlay.waystones.WaystoneMarker;
import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.event.fabric.FullscreenDisplayEvent;
import journeymap.client.api.model.IFullscreen;
import lombok.Getter;
import lombok.Setter;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import java.util.Comparator;

public class JMIFabricEventListener implements PlatformEventListener {

    @Getter @Setter
    private boolean firstLogin;
    private boolean haveDim;

    public void register() {
        ClientTickEvents.START_CLIENT_TICK.register(this::onClientTick);
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(this::onAddonButtonDisplay);
        ScreenEvents.AFTER_INIT.register(this::onGuiScreen);
    }

    public void onClientTick(Minecraft mc) {
        final var level = mc.level;

        if (level == null) {
            if (haveDim) {
                haveDim = false;
                ClaimedChunkPolygon.INSTANCE.getChunkData().clear();
                WaystoneMarker.INSTANCE.getWaystones().clear();
                JMI.LOGGER.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) firstLogin = haveDim = true;

        ClaimedChunkPolygon.INSTANCE.onClientTick();
    }

    public void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        var buttonDisplay = event.getThemeButtonDisplay();

        JMOverlayManager.INSTANCE.getToggleableOverlays().values().stream()
                .sorted(Comparator.comparing(o -> o.getOrder()))
                .forEach(t -> {
                    if (!t.isEnabled()) return;
                    buttonDisplay.addThemeToggleButton(t.getButtonLabel(), t.getButtonLabel(), t.getButtonIconName(), t.isActivated(), b -> t.onToggle(b));
                });
    }

    private void onGuiScreen(Minecraft minecraft, Screen screen, int i, int i1) {
        ScreenEvents.remove(screen).register(event -> ClaimingMode.INSTANCE.onGuiScreen(screen));

        if (screen instanceof IFullscreen) {
            ScreenMouseEvents.afterMouseRelease(screen).register((screenE, mouseX, mouseY, button) -> ClaimingMode.INSTANCE.getHandler().onMouseReleased(button));
            ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) -> GeneralDataOverlay.onScreenDraw(screen, stack));
        }
    }
}
