package me.frankv.jmi.compat.ftbchunks;

import journeymap.client.api.IClientAPI;
import lombok.Getter;
import me.frankv.jmi.api.ModCompat;
import me.frankv.jmi.api.PlatformHelper;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.Set;

@Getter
public class FTBChunksCompat implements ModCompat {

    private final Set<ToggleableOverlay> toggleableOverlays;
    private IClientConfig clientConfig;

    public FTBChunksCompat() {
        toggleableOverlays = Set.of(ClaimedChunkPolygon.INSTANCE, ClaimingMode.INSTANCE);
    }

    @Override
    public void init(IClientAPI jmAPI, IClientConfig clientConfig) {
        this.clientConfig = clientConfig;
        ClaimedChunkPolygon.INSTANCE.init(jmAPI, clientConfig);
        ClaimingMode.INSTANCE.init(jmAPI, clientConfig);
    }

    @Override
    public void registerEvent(JMIEventBus eventBus) {
        eventBus.subscribe(Event.ClientTick.class, e -> ClaimedChunkPolygon.INSTANCE.onClientTick());
        eventBus.subscribe(Event.ResetDataEvent.class, e -> ClaimedChunkPolygon.INSTANCE.getChunkData().clear());
        eventBus.subscribe(Event.ScreenClose.class, e -> ClaimingMode.INSTANCE.onScreenClose(e.screen()));
        eventBus.subscribe(Event.JMClientEvent.class, ClaimedChunkPolygon.INSTANCE::onJMEvent);
        eventBus.subscribe(Event.JMClientEvent.class, e -> ClaimingMode.INSTANCE.onJMEvent(e.clientEvent()));
        eventBus.subscribe(Event.MouseRelease.class, e -> ClaimingMode.INSTANCE.getHandler().onMouseReleased(e.button()));
        eventBus.subscribe(Event.ScreenDraw.class, e -> GeneralDataOverlay.onScreenDraw(e.screen(), e.guiGraphics()));
    }

    @Override
    public boolean isEnabled() {
        return clientConfig.getFtbChunks();
    }

    @Override
    public boolean isTargetModsLoaded() {
        return PlatformHelper.PLATFORM.isModLoaded("ftbchunks");
    }
}
