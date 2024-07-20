package me.frankv.jmi;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.client.JourneyMapPlugin;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.ModCompatFactory;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.OverlayInitErrorHandler;
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

        OverlayInitErrorHandler.handlers.put("ClammingMode", null);
        OverlayInitErrorHandler.handlers.put("ClaimedChunkPolygon", null);

        overlayFactory = new ModCompatFactory(jmAPI, JMI.getClientConfig(), JMI.getJmiEventBus());
        OverlayHelper.setJmAPI(jmAPI);
        JMI.getJmiEventBus().subscribe(Event.JMMappingEvent.class, this::onEvent);

        log.info("Initialized " + getClass().getName());
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

//    @Override
    public void onEvent(Event.JMMappingEvent event) {
        try {
            switch (event.mappingEvent().getStage()) {
                case MAPPING_STARTED -> JMI.setFirstLogin(false);

                case MAPPING_STOPPED -> {
                    jmAPI.removeAll(Constants.MOD_ID);
                    log.debug("all overlays removed");
                }
            }

//            JMI.getJmiEventBus().sendEvent(new Event.JMClientEvent(event, JMI.isFirstLogin()));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

    }

}
