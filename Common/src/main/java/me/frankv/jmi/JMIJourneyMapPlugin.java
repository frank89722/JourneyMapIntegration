package me.frankv.jmi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.OverlayInitErrorHandler;
import me.frankv.jmi.api.ModCompatFactory;
import me.frankv.jmi.util.OverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@Getter
@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
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
        jmAPI.subscribe(getModId(), EnumSet.of(MAPPING_STARTED, MAPPING_STOPPED, MAP_CLICKED, MAP_DRAGGED, MAP_MOUSE_MOVED, REGISTRY));

        log.info("Initialized " + getClass().getName());
    }

    @Override
    public String getModId() {
        return Constants.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case MAPPING_STARTED -> JMI.setFirstLogin(false);

                case MAPPING_STOPPED -> {
                    jmAPI.removeAll(Constants.MOD_ID);
                    log.debug("all overlays removed");
                }
            }

            JMI.getJmiEventBus().sendEvent(new Event.JMClientEvent(event, JMI.isFirstLogin()));
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }

    }

}
