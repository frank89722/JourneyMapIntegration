package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.FullscreenMapEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static frankv.jmi.JMIOverlayHelper.createPolygon;
import static frankv.jmi.JMIOverlayHelper.removePolygons;

public class ClaimingModeHandler {
    private static boolean doRecord = false;
    public static Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    public static HashSet<XZ> chunks = new HashSet<>();

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
        if (doRecord || ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) event.cancel();
    }

    public static void mouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!doRecord) return;

        var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z)) || chunks.contains(xz)) addToWaitingList(xz);
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

//    @SubscribeEvent
//    public static void mouse(InputEvent.MouseInputEvent event) {
//        if (!doRecord) return;
//        if (event.getAction() != GLFW.GLFW_RELEASE || event.getButton() > 1) return;
//
//        applyChanges(event.getButton());
//    }

    public static void onMouseReleased(int mouseButton) {
        if (!doRecord) return;
        if (mouseButton > 1) return;

        applyChanges(mouseButton);
    }
}
