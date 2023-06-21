package me.frankv.jmi.jmoverlay;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.event.ClientEvent;

public interface ToggleableOverlay {

    void init(IClientAPI jmAPI);
    void onToggle(IThemeButton button);
    void onJMEvent(ClientEvent event);

    boolean isEnabled();
    String getButtonLabel();
    String getButtonIconName();
    int getOrder();
    boolean isActivated();

}
