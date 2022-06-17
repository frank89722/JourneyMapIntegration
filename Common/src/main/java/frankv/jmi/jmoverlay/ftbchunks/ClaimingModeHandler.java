package frankv.jmi.jmoverlay.ftbchunks;

import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import frankv.jmi.JMI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.FullscreenMapEvent;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static frankv.jmi.util.JMIOverlayHelper.createPolygon;
import static frankv.jmi.util.JMIOverlayHelper.removePolygons;

public class ClaimingModeHandler {
    private boolean doRecord = false;

    @Getter
    private Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    private HashSet<XZ> chunks = new HashSet<>();

    public void onPreClick(FullscreenMapEvent.ClickEvent event) {
        if (!JMI.ftbchunks || !ClaimingMode.INSTANCE.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (!ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x, xz.z))) return;

        doRecord = true;
        addToWaitingList(xz);
        event.cancel();
    }

    public void onPreDrag(FullscreenMapEvent.MouseDraggedEvent event) {
        if (!JMI.ftbchunks || !ClaimingMode.INSTANCE.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (doRecord || ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x, xz.z))) event.cancel();
    }

    public void onMouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!JMI.ftbchunks || !doRecord) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x, xz.z)) || chunks.contains(xz)) addToWaitingList(xz);
    }

    private void addToWaitingList(XZ xz) {
        final var polygon = ClaimingMode.INSTANCE.dragPolygon(xz);
        if (!createPolygon(polygon)) return;

        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private void applyChanges(int mouseButton) {
        doRecord = false;
        if (chunks.isEmpty()) return;

        new RequestChunkChangePacket(Screen.hasShiftDown() ? mouseButton+2 : mouseButton, chunks).sendToServer();
        removePolygons(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F);
    }

    public void onMouseReleased(int mouseButton) {
        if (!JMI.ftbchunks || !doRecord || mouseButton > 1) return;

        applyChanges(mouseButton);
    }
}
