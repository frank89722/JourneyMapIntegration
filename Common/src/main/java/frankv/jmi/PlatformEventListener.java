package frankv.jmi;

import frankv.jmi.jmoverlay.JMOverlayManager;
import frankv.jmi.jmoverlay.ToggleableOverlay;
import frankv.jmi.jmoverlay.ftbchunks.ClaimedChunkPolygon;
import frankv.jmi.jmoverlay.waystones.WaystoneMarker;
import journeymap.client.api.display.ThemeButtonDisplay;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;

import java.util.Comparator;

public abstract class PlatformEventListener implements IPlatformEventListener {
    private static final Minecraft mc = Minecraft.getInstance();

    @Getter @Setter
    private boolean firstLogin;
    private boolean haveDim;

    protected void onClientTick() {
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

    protected void onAddonButtonDisplay(ThemeButtonDisplay buttonDisplay) {

        JMOverlayManager.INSTANCE.getToggleableOverlays().values().stream()
                .sorted(Comparator.comparing(ToggleableOverlay::getOrder))
                .forEach(t -> {
                    if (!t.isEnabled()) return;
                    buttonDisplay.addThemeToggleButton(t.getButtonLabel(), t.getButtonLabel(), t.getButtonIconName(), t.isActivated(), b -> t.onToggle(b));
                });
    }
}
