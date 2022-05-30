package frankv.jmi;

import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.ftbchunks.client.ClaimingMode;
import frankv.jmi.ftbchunks.client.ClaimingModeHandler;
import frankv.jmi.ftbchunks.client.GeneralDataOverlay;
import frankv.jmi.waypointmessage.WaypointChatMessage;
import frankv.jmi.waystones.client.WaystoneMarker;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import net.blay09.mods.balm.api.event.client.screen.ScreenDrawEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.lwjgl.glfw.GLFW;

import static frankv.jmi.ftbchunks.client.ClaimingMode.activated;
import static frankv.jmi.ftbchunks.client.ClaimingMode.buttonControl;

public class JMIForgeEventListener implements PlatformEventListener {
    private static final Minecraft mc = Minecraft.getInstance();

    @Getter @Setter
    private boolean firstLogin;
    private boolean haveDim;

    public void register() {
        MinecraftForge.EVENT_BUS.addListener(this::onClientTick);
        MinecraftForge.EVENT_BUS.addListener(this::onAddonButtonDisplay);
        MinecraftForge.EVENT_BUS.addListener(this::onGuiScreen);
        MinecraftForge.EVENT_BUS.addListener(this::onMouse);
        MinecraftForge.EVENT_BUS.addListener(this::onScreenDraw);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerInteract);
    }

    public void onClientTick(TickEvent.ClientTickEvent event) {
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

    public void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        if (JMI.ftbchunks) {
            var buttonDisplay = event.getThemeButtonDisplay();
            buttonDisplay.addThemeToggleButton("FTBChunks Claiming Mode", "FTBChunks Claiming Mode", "grid", activated, b -> buttonControl(b));
        }
    }

    public void onGuiScreen(ScreenEvent event) {
        if (JMI.ftbchunks) {
            ClaimingMode.onGuiScreen(event.getScreen());
        }
    }

    public void onMouse(InputEvent.MouseInputEvent event) {
        if (JMI.ftbchunks) {
            if (event.getAction() == GLFW.GLFW_RELEASE) {
                ClaimingModeHandler.onMouseReleased(event.getButton());
            }
        }
    }

    public void onScreenDraw(ScreenDrawEvent.Post event) {
        if (JMI.ftbchunks) {
            GeneralDataOverlay.onScreenDraw(event.getScreen(), event.getPoseStack());
        }
    }

    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        if (JMI.ftbchunks) {
            WaypointChatMessage.onRightClickOnBlock(event.getPos(), event.getItemStack());
        }
    }
}
