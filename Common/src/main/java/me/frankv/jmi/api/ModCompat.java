package me.frankv.jmi.api;

import journeymap.api.v2.client.IClientAPI;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.Set;

public interface ModCompat {

    void init(IClientAPI jmAPI, ClientConfig clientConfig);

    void registerEvent(JMIEventBus eventBus);

    Set<ToggleableOverlay> getToggleableOverlays();

    boolean isEnabled();

    boolean isTargetModsLoaded();
}
