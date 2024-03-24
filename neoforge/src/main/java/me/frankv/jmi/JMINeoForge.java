package me.frankv.jmi;

import journeymap.client.api.event.neoforge.FullscreenDisplayEvent;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.config.ClientConfig;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import me.frankv.jmi.waypointmessage.WaypointChatMessage;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.IExtensionPoint;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

@Mod(Constants.MOD_ID)
public class JMINeoForge {
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public JMINeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::setupClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());
        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () ->
                new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (a, b) -> true));
    }

    private void setupClient(final FMLClientSetupEvent event) {
        JMI.init(CLIENT_CONFIG);
        new JMDefaultConfig().tryWriteJMDefaultConfig();
        registerEvent();
    }

    private void registerEvent() {
        var eventBus = JMI.getJmiEventBus();
        NeoForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) ->
                eventBus.sendEvent(new Event.ClientTick()));
        NeoForge.EVENT_BUS.addListener((FullscreenDisplayEvent.AddonButtonDisplayEvent e) ->
                eventBus.sendEvent(new Event.AddButtonDisplay(e.getThemeButtonDisplay())));
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

//        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) ->
//                eventBus.sendEvent(new Event.PlayerInteract(e.getPos(), e.getItemStack())));
    }
}
