package me.frankv.jmi;

import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.config.NeoForgeClientConfig;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import me.frankv.jmi.waypointmessage.WaypointChatMessage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

@Mod(Constants.MOD_ID)
public class JMINeoForge {
    public static final NeoForgeClientConfig CLIENT_CONFIG = new NeoForgeClientConfig();

    public JMINeoForge(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::setupClient);
        modContainer.registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());
    }

    private void setupClient(final FMLClientSetupEvent event) {
        JMI.init(CLIENT_CONFIG);
        new JMDefaultConfig().tryWriteJMDefaultConfig();
        registerEvent();
    }

    private void registerEvent() {
        var eventBus = JMI.getJmiEventBus();
        NeoForge.EVENT_BUS.addListener((ClientTickEvent.Pre e) ->
                eventBus.sendEvent(new Event.ClientTick()));
        NeoForge.EVENT_BUS.addListener((ScreenEvent.Closing e) ->
                eventBus.sendEvent(new Event.ScreenClose(e.getScreen())));
        NeoForge.EVENT_BUS.addListener((InputEvent.MouseButton.Post e) -> {
            if (e.getAction() == GLFW.GLFW_RELEASE) {
                eventBus.sendEvent(new Event.MouseRelease(e.getButton()));
            }
        });
        NeoForge.EVENT_BUS.addListener((ScreenEvent.Render.Post e) ->
                eventBus.sendEvent(new Event.ScreenDraw(e.getScreen(), e.getGuiGraphics())));
        NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) ->
                WaypointChatMessage.onRightClickOnBlock(e.getPos(), e.getItemStack()));

    }
}
