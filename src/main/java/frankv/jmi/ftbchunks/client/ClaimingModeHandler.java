package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.FullscreenMapEvent;
import journeymap.client.api.event.forge.PopupMenuEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static frankv.jmi.JMIOverlayHelper.*;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClaimingModeHandler {
    private static boolean doRecord = false;
    private static Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    private static HashSet<XZ> chunks = new HashSet<>();

    public static void preClick(FullscreenMapEvent.ClickEvent event) {
        if (!ClaimingMode.activated) return;
        var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());

        if (!ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) return;
        doRecord = true;
        addToWaitingList(xz);
        event.cancel();
    }

    public static void preDrag(FullscreenMapEvent.MouseDraggedEvent event) {
        if (!ClaimingMode.activated) return;
        var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());

        if (!ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) return;
        event.cancel();
    }

    public static void mouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!doRecord) return;

        var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (!ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) return;
        if (chunks.contains(xz)) return;
        addToWaitingList(xz);
    }

    private static void addToWaitingList(XZ xz) {
        var polygon = ClaimingMode.dragPolygon(xz);
        if (!createPolygon(polygon)) return;
        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private static void applyChanges(int mouseButton) {
        doRecord = false;

        if (chunks.isEmpty()) return;
        new RequestChunkChangePacket(Screen.hasShiftDown() ? mouseButton+2 : mouseButton, chunks).sendToServer();
        removePolygons(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
    }

    @SubscribeEvent
    public static void mouse(InputEvent.MouseInputEvent event) {
        if (!doRecord) return;
        if (event.getAction() != GLFW.GLFW_RELEASE) return;
        if (event.getButton() > 1) return;
        applyChanges(event.getButton());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPopMenu(PopupMenuEvent event) {
        if (ClaimingMode.activated) event.setCanceled(true);
    }
}
