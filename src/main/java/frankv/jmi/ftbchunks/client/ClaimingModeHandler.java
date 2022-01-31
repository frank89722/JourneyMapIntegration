package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import frankv.jmi.JMIOverlayHelper;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.FullscreenMapEvent;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClaimingModeHandler {
    private static boolean doRecord = false;
    public static Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    public static HashSet<XZ> chunks = new HashSet<>();

    public static void preClick(FullscreenMapEvent.ClickEvent event) {
        if (!ClaimingMode.activated) return;
        XZ xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());

        if (!ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) return;
        doRecord = true;
        addToWaitingList(xz);
        event.cancel();
    }

    public static void preDrag(FullscreenMapEvent.MouseDraggedEvent event) {
        if (!ClaimingMode.activated) return;
        XZ xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());

        if (doRecord || ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z))) event.cancel();
    }

    public static void mouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!doRecord) return;

        XZ xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (ClaimingMode.area.contains(new ChunkPos(xz.x, xz.z)) || chunks.contains(xz)) addToWaitingList(xz);
    }

    private static void addToWaitingList(XZ xz) {
        PolygonOverlay polygon = ClaimingMode.dragPolygon(xz);
        if (!JMIOverlayHelper.createPolygon(polygon)) return;
        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private static void applyChanges(int mouseButton) {
        doRecord = false;

        if (chunks.isEmpty()) return;
        new RequestChunkChangePacket(Screen.hasShiftDown() ? mouseButton+2 : mouseButton, chunks).sendToServer();
        JMIOverlayHelper.removePolygons(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
    }

    @SubscribeEvent
    public static void mouse(InputEvent.MouseInputEvent event) {
        if (!doRecord) return;
        if (event.getAction() != GLFW.GLFW_RELEASE || event.getButton() > 1) return;

        applyChanges(event.getButton());
    }
}
