package me.frankv.jmi.compat.twilightforest;

import journeymap.api.v2.client.IClientAPI;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.ModCompat;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

import java.util.Set;

@Slf4j
public class TwilightForestCompat implements ModCompat {
    private ClientConfig clientConfig;
//    private final Set<ToggleableOverlay> toggleableOverlays = Set.of(WaystonesMarker.INSTANCE);

    @Override
    public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
        this.clientConfig = clientConfig;
//        WaystonesMarker.INSTANCE.init(jmAPI, clientConfig);
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        log.info("TwilightForestCompat init");
        MouseHandler mouseHandler = Minecraft.getInstance().mouseHandler;

    }

    @Override
    public void registerEvent(JMIEventBus eventBus) {
//        eventBus.subscribe(Event.JMMappingEvent.class, WaystonesMarker.INSTANCE::onJMMapping);
//        eventBus.subscribe(Event.ResetDataEvent.class, e -> WaystonesMarker.INSTANCE.getWaystones().clear());
    }

    @Override
    public Set<ToggleableOverlay> getToggleableOverlays() {
        return Set.of();
    }

    @Override
    public boolean isEnabled() {
//        return clientConfig.getWaystone();
        return true;
    }

    @Override
    public boolean isTargetModsLoaded() {
        return true;
//        boolean b = (PlatformHelper.PLATFORM.isModLoaded("balm") || PlatformHelper.PLATFORM.isModLoaded("balm-fabric")) &&
//                (PlatformHelper.PLATFORM.isModLoaded("waystones") || PlatformHelper.PLATFORM.isModLoaded("waystones-fabric"));
//
//        if (b) {
//            try {
//                WaystonesListReceivedEvent.class.getClass();
//            } catch (NoClassDefFoundError e) {
//                return false;
//            }
//        }
//        return b;
    }
}
