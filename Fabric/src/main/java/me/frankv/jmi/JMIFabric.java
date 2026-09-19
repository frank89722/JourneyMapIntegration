package me.frankv.jmi;

import journeymap.api.v2.client.fullscreen.IFullscreen;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.config.FabricClientConfig;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.gui.screens.Screen;

public class JMIFabric implements ClientModInitializer {
    public static final FabricClientConfig CLIENT_CONFIG = FabricClientConfig.loadConfig();

    @Override
    public void onInitializeClient() {
        JMI.init(CLIENT_CONFIG);
        new JMDefaultConfig().tryWriteJMDefaultConfig();
        registerEvent();
    }

    private static void registerEvent() {
        var eventBus = JMI.getJmiEventBus();
        ClientTickEvents.START_CLIENT_TICK.register(mc -> eventBus.sendEvent(new Event.ClientTick()));
        ScreenEvents.AFTER_INIT.register((mc, screen, width, height) -> onGuiScreen(screen, eventBus));
    }

    private static void onGuiScreen(Screen screen, JMIEventBus eventBus) {
        ScreenEvents.remove(screen).register(s -> eventBus.sendEvent(new Event.ScreenClose(screen)));
        if (!(screen instanceof IFullscreen)) return;

        ScreenMouseEvents.afterMouseRelease(screen).register((s, mouseX, mouseY, button) ->
                eventBus.sendEvent(new Event.MouseRelease(button)));

        ScreenEvents.afterRender(screen).register((s, guiGraphics, mouseX, mouseY, tickDelta) ->
                eventBus.sendEvent(new Event.ScreenDraw(screen, guiGraphics)));
    }
}
