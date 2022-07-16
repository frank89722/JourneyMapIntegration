package frankv.jmi;

import frankv.jmi.jmoverlay.ftbchunks.ClaimingMode;
import frankv.jmi.jmoverlay.ftbchunks.GeneralDataOverlay;
import frankv.jmi.waypointmessage.WaypointChatMessage;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

public class JMIForgeEventListener extends PlatformEventListener {

    public void register() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientTickEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onAddonButtonDisplayEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onGuiScreen);
        MinecraftForge.EVENT_BUS.addListener(this::onMouse);
        MinecraftForge.EVENT_BUS.addListener(this::onScreenDraw);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerInteract);
    }

    private void onClientTickEvent(TickEvent.ClientTickEvent event) {
        onClientTick();
    }

    private void onAddonButtonDisplayEvent(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        onAddonButtonDisplay(event.getThemeButtonDisplay());
    }

    private void onGuiScreen(ScreenEvent event) {
        ClaimingMode.INSTANCE.onGuiScreen(event.getScreen());
    }

    private void onMouse(InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_RELEASE) {
            ClaimingMode.INSTANCE.getHandler().onMouseReleased(event.getButton());
        }
    }

    private void onScreenDraw(ScreenEvent.DrawScreenEvent.Post event) {
        GeneralDataOverlay.onScreenDraw(event.getScreen(), event.getPoseStack());
    }

    private void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        WaypointChatMessage.onRightClickOnBlock(event.getPos(), event.getItemStack());
    }
}
