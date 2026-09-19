package me.frankv.jmi.api.jmoverlay;

import journeymap.api.v2.client.fullscreen.IThemeButton;
import net.minecraft.resources.ResourceLocation;

public interface ToggleableOverlay {

    void onToggle(IThemeButton button);

    String getButtonLabel();

    ResourceLocation getButtonIconName();

    int getOrder();

    boolean isActivated();
}
