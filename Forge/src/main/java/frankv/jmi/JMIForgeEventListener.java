package frankv.jmi;

import frankv.jmi.jmoverlay.JMOverlayManager;
import frankv.jmi.jmoverlay.ftbchunks.ClaimedChunkPolygon;
import frankv.jmi.jmoverlay.ftbchunks.ClaimingMode;
import frankv.jmi.jmoverlay.ftbchunks.GeneralDataOverlay;
import frankv.jmi.waypointmessage.WaypointChatMessage;
import frankv.jmi.jmoverlay.waystones.WaystoneMarker;
import journeymap.client.api.event.forge.FullscreenDisplayEvent;
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

import java.util.Comparator;

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
        final var level = mc.level;

        if (level == null) {
            if (haveDim) {
                haveDim = false;
                ClaimedChunkPolygon.INSTANCE.getChunkData().clear();
                WaystoneMarker.INSTANCE.getWaystones().clear();
                JMI.LOGGER.debug("all data cleared");
            }
            return;
        }

        if (!haveDim) firstLogin = haveDim = true;

        ClaimedChunkPolygon.INSTANCE.onClientTick();
    }

    public void onAddonButtonDisplay(FullscreenDisplayEvent.AddonButtonDisplayEvent event) {
        final var buttonDisplay = event.getThemeButtonDisplay();

        JMOverlayManager.INSTANCE.getToggleableOverlays().values().stream()
                .sorted(Comparator.comparing(o -> o.getOrder()))
                .forEach(t -> {
                    if (!t.isEnabled()) return;
                    buttonDisplay.addThemeToggleButton(t.getButtonLabel(), t.getButtonLabel(), t.getButtonIconName(), t.isActivated(), b -> t.onToggle(b));
                });
    }

    public void onGuiScreen(ScreenEvent event) {
        ClaimingMode.INSTANCE.onGuiScreen(event.getScreen());
    }

    public void onMouse(InputEvent.MouseInputEvent event) {
        if (event.getAction() == GLFW.GLFW_RELEASE) {
            ClaimingMode.INSTANCE.getHandler().onMouseReleased(event.getButton());
        }
    }

    public void onScreenDraw(ScreenDrawEvent.Post event) {
        GeneralDataOverlay.onScreenDraw(event.getScreen(), event.getPoseStack());
    }

    public void onPlayerInteract(PlayerInteractEvent.RightClickBlock event) {
        WaypointChatMessage.onRightClickOnBlock(event.getPos(), event.getItemStack());
    }
}
