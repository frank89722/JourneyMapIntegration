package me.frankv.jmi;

import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.config.ClientConfig;
import me.frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import me.frankv.jmi.waypointmessage.WaypointChatMessage;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;

@Mod(Constants.MOD_ID)
public class JMIForge {
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public JMIForge() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
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
        MinecraftForge.EVENT_BUS.addListener((TickEvent.ClientTickEvent e) ->
                eventBus.sendEvent(new Event.ClientTick()));
        MinecraftForge.EVENT_BUS.addListener((FullscreenDisplayEvent.AddonButtonDisplayEvent e) ->
                eventBus.sendEvent(new Event.AddButtonDisplay(e.getThemeButtonDisplay())));
        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Closing e) ->
                eventBus.sendEvent(new Event.ScreenClose(e.getScreen())));
        MinecraftForge.EVENT_BUS.addListener((InputEvent.MouseButton e) -> {
                    if (e.getAction() == GLFW.GLFW_RELEASE) {
                        eventBus.sendEvent(new Event.MouseRelease(e.getButton()));
                    }
                });
        MinecraftForge.EVENT_BUS.addListener((ScreenEvent.Render.Post e) ->
                eventBus.sendEvent(new Event.ScreenDraw(e.getScreen(), e.getGuiGraphics())));
        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) ->
                WaypointChatMessage.onRightClickOnBlock(e.getPos(), e.getItemStack()));

//        MinecraftForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickBlock e) ->
//                eventBus.sendEvent(new Event.PlayerInteract(e.getPos(), e.getItemStack())));
    }
}
