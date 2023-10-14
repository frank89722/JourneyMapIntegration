package me.frankv.jmi;

import journeymap.client.api.event.fabric.FabricEvents;
import journeymap.client.api.model.IFullscreen;
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
        FabricEvents.ADDON_BUTTON_DISPLAY_EVENT.register(e -> eventBus.sendEvent(new Event.AddButtonDisplay(e.getThemeButtonDisplay())));
        ScreenEvents.AFTER_INIT.register((minecraft, screen, i, i1) -> onGuiScreen(minecraft, screen, eventBus));
    }

    private static void onGuiScreen(Minecraft minecraft, Screen screen, JMIEventBus eventBus) {
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
