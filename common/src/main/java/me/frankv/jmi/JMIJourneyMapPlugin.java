package me.frankv.jmi;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;
import journeymap.api.v2.common.event.FullscreenEventRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.ModCompatFactory;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.util.OverlayHelper;

import javax.annotation.ParametersAreNonnullByDefault;


@Getter
@ParametersAreNonnullByDefault
@JourneyMapPlugin(apiVersion = "2.0.0")
@Slf4j
public class JMIJourneyMapPlugin implements IClientPlugin {

    private IClientAPI jmAPI;

    private ModCompatFactory overlayFactory;

    @Override
    public void initialize(final IClientAPI jmAPI) {
        this.jmAPI = jmAPI;

        registerJMEvents();

        overlayFactory = new ModCompatFactory(jmAPI, JMI.getClientConfig(), JMI.getJmiEventBus());
        OverlayHelper.setJmAPI(jmAPI);
        JMI.getJmiEventBus().subscribe(Event.JMMappingEvent.class, this::onJMMapping);

        log.info("Initialized {}", getClass().getName());
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    private void registerJMEvents() {
        var eventBus = JMI.getJmiEventBus();
        FullscreenEventRegistry.ADDON_BUTTON_DISPLAY_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.AddButtonDisplay(e.getThemeButtonDisplay())));
        ClientEventRegistry.INFO_SLOT_REGISTRY_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMInfoSlotRegistryEvent(e)));
        ClientEventRegistry.MAPPING_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMappingEvent(e, JMI.isFirstLogin())));
        FullscreenEventRegistry.FULLSCREEN_MAP_MOVE_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMouseMoveEvent(e)));
        FullscreenEventRegistry.FULLSCREEN_MAP_DRAG_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMouseDraggedEvent(e)));
        FullscreenEventRegistry.FULLSCREEN_MAP_CLICK_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMClickEvent(e)));

    }

    private void onJMMapping(Event.JMMappingEvent event) {

        switch (event.mappingEvent().getStage()) {
            case MAPPING_STARTED -> JMI.setFirstLogin(false);
            case MAPPING_STOPPED -> {
                jmAPI.removeAll(Constants.MOD_ID);
                log.debug("all overlays removed");
            }
        }

    }

}
