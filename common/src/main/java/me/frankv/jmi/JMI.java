package me.frankv.jmi;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.util.OverlayHelper;
import net.minecraft.client.Minecraft;

@Slf4j
public class JMI {
    @Getter
    private static JMIEventBus jmiEventBus;
    @Getter
    private static IClientConfig clientConfig;

    private static final Minecraft mc = Minecraft.getInstance();

    @Getter @Setter
    private static boolean haveDim = false;
    @Getter @Setter
    private static boolean firstLogin = false;


    public static void init(IClientConfig clientConfig) {
        JMI.clientConfig = clientConfig;
        jmiEventBus = new JMIEventBus();
        jmiEventBus.subscribe(Event.ClientTick.class, e -> onClientTick());
        jmiEventBus.subscribe(Event.JMClientEvent.class, OverlayHelper::onJMEvent);
    }


    private static void onClientTick() {
        if (mc.level == null) {
            if (haveDim) {
                haveDim = false;
                jmiEventBus.sendEvent(new Event.ResetDataEvent());
                log.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) {
            firstLogin = true;
            haveDim = true;
        }
    }


}