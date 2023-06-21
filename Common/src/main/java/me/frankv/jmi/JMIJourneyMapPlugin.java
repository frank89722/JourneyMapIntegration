package me.frankv.jmi;

import me.frankv.jmi.jmoverlay.JMOverlayManager;
import me.frankv.jmi.jmoverlay.waystones.WaystoneMarker;
import me.frankv.jmi.util.OverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JMIJourneyMapPlugin implements IClientPlugin {
    private IClientAPI jmAPI;

    @Override
    public void initialize(final IClientAPI jmAPI) {
        this.jmAPI = jmAPI;

        JMI.platformEventListener.register();
        JMOverlayManager.INSTANCE.setJmAPI(jmAPI);
        OverlayHelper.setJmAPI(jmAPI);
        jmAPI.subscribe(getModId(), EnumSet.of(MAPPING_STARTED, MAPPING_STOPPED, MAP_CLICKED, MAP_DRAGGED, MAP_MOUSE_MOVED, REGISTRY));

        try {
            if (JMI.waystones) {
                Balm.getEvents().onEvent(KnownWaystonesEvent.class, WaystoneMarker.INSTANCE::onKnownWaystones);
            }
        } catch (NoClassDefFoundError e) {
            JMI.waystones = false;
        }
        JMI.LOGGER.info("Initialized " + getClass().getName());
    }

    @Override
    public String getModId() {
        return JMI.MOD_ID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case MAPPING_STARTED -> JMI.platformEventListener.setFirstLogin(false);

                case MAPPING_STOPPED -> {
                    jmAPI.removeAll(JMI.MOD_ID);
                    JMI.LOGGER.debug("all overlays removed");
                }
            }

            JMOverlayManager.INSTANCE.getToggleableOverlays().values().forEach(o -> o.onJMEvent(event));

        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }

    }
}
