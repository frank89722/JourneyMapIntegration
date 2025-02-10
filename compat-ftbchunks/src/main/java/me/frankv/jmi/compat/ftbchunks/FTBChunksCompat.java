package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import journeymap.api.v2.client.IClientAPI;
import lombok.Getter;
import me.frankv.jmi.api.ModCompat;
import me.frankv.jmi.api.PlatformHelper;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.compat.ftbchunks.claimingmode.ClaimingMode;
import me.frankv.jmi.compat.ftbchunks.infoslot.ClaimedChunkInfoSlot;

import java.util.Set;

@Getter
public class FTBChunksCompat implements ModCompat {

    private final Set<ToggleableOverlay> toggleableOverlays = Set.of(ClaimedChunksOverlay.INSTANCE, ClaimingMode.INSTANCE);
    private ClaimedChunkInfoSlot claimedChunkInfoSlot;
    private ClaimedChunksOverlayStates states;

    private ClientConfig clientConfig;


    @Override
    public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
        this.states = new ClaimedChunksOverlayStates();
        this.clientConfig = clientConfig;
        this.claimedChunkInfoSlot = new ClaimedChunkInfoSlot(states);
        ClaimedChunksOverlay.INSTANCE.init(clientConfig, states);

        disableFTBChunksStuff();
    }

    @Override
    public void registerEvent(JMIEventBus eventBus) {
        eventBus.subscribe(Event.ClientTick.class, e -> ClaimedChunksOverlay.INSTANCE.onClientTick());
        eventBus.subscribe(Event.ResetDataEvent.class, e -> states.getChunkData().clear());
        eventBus.subscribe(Event.ScreenClose.class, e -> ClaimingMode.INSTANCE.onScreenClose(e.screen()));
        eventBus.subscribe(Event.JMMappingEvent.class, ClaimedChunksOverlay.INSTANCE::onJMMapping);
        eventBus.subscribe(Event.JMClickEvent.class, e -> ClaimingMode.INSTANCE.getHandler().onClick(e.clickEvent()));
        eventBus.subscribe(Event.JMMouseMoveEvent.class, e -> ClaimingMode.INSTANCE.getHandler().onMouseMove(e.mouseMoveEvent()));
        eventBus.subscribe(Event.JMMouseDraggedEvent.class, e -> ClaimingMode.INSTANCE.getHandler().onDrag(e.mouseDraggedEvent()));
        eventBus.subscribe(Event.JMInfoSlotRegistryEvent.class, claimedChunkInfoSlot::onJMInfoSlotRegistryEvent);
        eventBus.subscribe(Event.MouseRelease.class, e -> ClaimingMode.INSTANCE.getHandler().onMouseReleased());
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

    private void disableFTBChunksStuff() {
        if (!clientConfig.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }
}
