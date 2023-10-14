package me.frankv.jmi.api;

import journeymap.client.api.IClientAPI;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.Set;

public interface ModCompat {

    void init(IClientAPI jmAPI, IClientConfig clientConfig);
    void registerEvent(JMIEventBus eventBus);

    Set<ToggleableOverlay> getToggleableOverlays();

    boolean isEnabled();

    boolean isTargetModsLoaded();
}
