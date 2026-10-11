package me.frankv.jmi.api.jmoverlay;

import journeymap.api.v2.client.fullscreen.IThemeButton;
import net.minecraft.resources.Identifier;

public interface ToggleableOverlay {

    void onToggle(IThemeButton button);

    String getButtonLabel();

    Identifier getButtonIcon();

    int getOrder();

    boolean isActivated();
}
