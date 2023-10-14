package me.frankv.jmi;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
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

//        Configs.waystones = PlatformHelper.PLATFORM.isModLoaded("waystones");
//        Configs.ftbchunks = PlatformHelper.PLATFORM.isModLoaded("ftbchunks");
//
//        if (Configs.ftbchunks) log.info("JMI FTBChunks compat loaded.");
//        if (Configs.waystones) log.info("JMI Waystones compat loaded.");

        jmiEventBus.subscribe(Event.ClientTick.class, e -> onClientTick());
    }


    private static void onClientTick() {
        if (mc.level == null) {
            if (haveDim) {
                haveDim = false;
                jmiEventBus.sendEvent(new Event.ResetDataEvent());
                log.debug("all data cleared");
            }
        }

        if (!haveDim) firstLogin = haveDim = true;
    }


}