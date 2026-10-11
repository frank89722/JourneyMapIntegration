package me.frankv.jmi;

import journeymap.api.v2.client.fullscreen.IFullscreen;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.config.FabricClientConfig;
import me.frankv.jmi.config.FiberUtils;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.Screen;

public class JMIFabric implements ClientModInitializer {
    public final static FabricClientConfig CLIENT_CONFIG = new FabricClientConfig();

    private static void registerEvent() {
        var eventBus = JMI.getJmiEventBus();
        ClientTickEvents.START_CLIENT_TICK.register(_ -> eventBus.sendEvent(new Event.ClientTick()));
        ScreenEvents.AFTER_INIT.register((_, screen, _, _) -> onGuiScreen(screen, eventBus));
    }

    private static void onGuiScreen(Screen screen, JMIEventBus eventBus) {
        ScreenEvents.remove(screen).register(_ -> eventBus.sendEvent(new Event.ScreenClose(screen)));
        if (!(screen instanceof IFullscreen)) return;

        ScreenMouseEvents.afterMouseRelease(screen).register((_, event, consumed) -> {
            eventBus.sendEvent(new Event.MouseRelease(event.button()));
            return consumed;
        });

        ScreenEvents.afterExtract(screen).register((_, graphics, _, _, _) ->
                eventBus.sendEvent(new Event.ScreenDraw(screen, graphics)));
    }

    @Override
    public void onInitializeClient() {
        FiberUtils.setup(CLIENT_CONFIG.getConfigTree(), FabricClientConfig.fileName);
        JMI.init(CLIENT_CONFIG);
        new JMDefaultConfig().tryWriteJMDefaultConfig();
        registerEvent();
    }

}
