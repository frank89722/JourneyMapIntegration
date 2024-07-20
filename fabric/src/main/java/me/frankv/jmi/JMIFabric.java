package me.frankv.jmi;

import journeymap.api.v2.client.fullscreen.IFullscreen;
import journeymap.api.v2.common.event.ClientEventRegistry;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.config.ClientConfig;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

public class JMIFabric implements ClientModInitializer {
    public static final IClientConfig CLIENT_CONFIG = ClientConfig.loadConfig();

    @Override
    public void onInitializeClient() {
        JMI.init(CLIENT_CONFIG);
        new JMDefaultConfig().tryWriteJMDefaultConfig();
        registerEvent();
    }

    public static void registerEvent() {
        var eventBus = JMI.getJmiEventBus();
        ClientTickEvents.START_CLIENT_TICK.register(__ -> eventBus.sendEvent(new Event.ClientTick()));
        ClientEventRegistry.MAPPING_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMappingEvent(e, JMI.isFirstLogin())));
        ClientEventRegistry.FULLSCREEN_MAP_MOVE_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMouseMoveEvent(e)));
        ClientEventRegistry.FULLSCREEN_MAP_DRAG_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMMouseDraggedEvent(e)));
        ClientEventRegistry.FULLSCREEN_MAP_CLICK_EVENT.subscribe(Constants.MOD_ID, e ->
                eventBus.sendEvent(new Event.JMClickEvent(e)));
        ScreenEvents.AFTER_INIT.register((minecraft, screen, i, i1) -> onGuiScreen(screen, eventBus));
    }

    private static void onGuiScreen(Screen screen, JMIEventBus eventBus) {
//        ScreenEvents.remove(screen).register(event -> ClaimingMode.INSTANCE.onGuiScreen(screen));
        ScreenEvents.remove(screen).register(event -> eventBus.sendEvent(new Event.ScreenClose(screen)));
        if (!(screen instanceof IFullscreen)) return;
//        ScreenMouseEvents.afterMouseRelease(screen).register((screenE, mouseX, mouseY, button) -> ClaimingMode.INSTANCE.getHandler().onMouseReleased(button));
        ScreenMouseEvents.afterMouseRelease(screen).register((screenE, mouseX, mouseY, button) ->
                eventBus.sendEvent(new Event.MouseRelease(GLFW.GLFW_RELEASE)));
//        ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) -> GeneralDataOverlay.onScreenDraw(screen, stack));
        ScreenEvents.afterRender(screen).register((screenE, stack, mouseX, mouseY, tickDelta) ->
                eventBus.sendEvent(new Event.ScreenDraw(screen, stack)));
    }
}
