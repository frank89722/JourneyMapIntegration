package me.frankv.jmi.compat.waystones;

import journeymap.client.api.IClientAPI;
import me.frankv.jmi.api.ModCompat;
import me.frankv.jmi.api.PlatformHelper;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;

import java.util.Set;

public class WaystonesCompat implements ModCompat {
    private IClientConfig clientConfig;
    private final Set<ToggleableOverlay> toggleableOverlays = Set.of(WaystonesMarker.INSTANCE);

    @Override
    public void init(IClientAPI jmAPI, IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        WaystonesMarker.INSTANCE.init(jmAPI, clientConfig);
    }

    @Override
    public void registerEvent(JMIEventBus eventBus) {
        eventBus.subscribe(Event.JMClientEvent.class, e -> WaystonesMarker.INSTANCE.onJMEvent(e.clientEvent()));
        eventBus.subscribe(Event.ResetDataEvent.class, e -> WaystonesMarker.INSTANCE.getWaystones().clear());
    }

    @Override
    public Set<ToggleableOverlay> getToggleableOverlays() {
        return toggleableOverlays;
    }

    @Override
    public boolean isEnabled() {
        return clientConfig.getWaystone();
    }

    @Override
    public boolean isTargetModsLoaded() {
        boolean b = (PlatformHelper.PLATFORM.isModLoaded("balm") || PlatformHelper.PLATFORM.isModLoaded("balm-fabric")) &&
                (PlatformHelper.PLATFORM.isModLoaded("waystones") || PlatformHelper.PLATFORM.isModLoaded("waystones-fabric"));

        if (b) {
            try {
                WaystonesListReceivedEvent.class.getClass();
            } catch (NoClassDefFoundError e) {
                return false;
            }
        }
        return b;
    }
}
