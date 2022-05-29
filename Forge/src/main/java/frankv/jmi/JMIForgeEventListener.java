package frankv.jmi;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.ftbchunks.client.ClaimingMode;
import frankv.jmi.ftbchunks.client.ClaimingModeHandler;
import frankv.jmi.ftbchunks.client.GeneralDataOverlay;
import frankv.jmi.waystones.client.WaystoneMarker;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import static frankv.jmi.ftbchunks.client.ClaimingMode.activated;
import static frankv.jmi.ftbchunks.client.ClaimingMode.buttonControl;

public class JMIForgeEventListener {
    private static final Minecraft mc = Minecraft.getInstance();
    public static boolean firstLogin;
    private static boolean haveDim;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        var level = mc.level;

        if (level == null) {
            if (haveDim) {
                haveDim = false;
                ClaimedChunkPolygon.chunkData.clear();
                WaystoneMarker.waystones.clear();
                JMI.LOGGER.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) firstLogin = haveDim = true;

        if (JMI.ftbchunks) {
            ClaimedChunkPolygon.onClientTick();
        }
    }

    @SubscribeEvent
    public static void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        if (JMI.ftbchunks) {
            var buttonDisplay = event.getThemeButtonDisplay();
            buttonDisplay.addThemeToggleButton("FTBChunks Claiming Mode", "FTBChunks Claiming Mode", "grid", activated, b -> buttonControl(b));
        }
    }

    @SubscribeEvent
    public static void onGuiScreen(ScreenEvent event) {
        if (JMI.ftbchunks) {
            ClaimingMode.onGuiScreen(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void mouse(InputEvent.MouseInputEvent event) {
        if (JMI.ftbchunks) {
            if (event.getAction() != GLFW.GLFW_RELEASE) return;
            ClaimingModeHandler.onMouseReleased(event.getButton());
        }
    }

    @SubscribeEvent
    public static void onScreenDraw(ScreenDrawEvent.Post event) {
        if (JMI.ftbchunks) {
            GeneralDataOverlay.onScreenDraw(event.getScreen(), event.getPoseStack());
        }
    }
}
