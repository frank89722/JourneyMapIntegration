package me.frankv.jmi.api.jmoverlay;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.event.ClientEvent;
import me.frankv.jmi.api.event.JMIEventBus;

import java.util.Map;

public interface ToggleableOverlay {

    void onToggle(IThemeButton button);

    String getButtonLabel();
    String getButtonIconName();
    int getOrder();
    boolean isActivated();
}
