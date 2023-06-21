package me.frankv.jmi.jmoverlay.ftbchunks;

import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import me.frankv.jmi.JMI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.FullscreenMapEvent;
import lombok.Getter;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static me.frankv.jmi.util.OverlayHelper.showOverlay;
import static me.frankv.jmi.util.OverlayHelper.removeOverlays;

public class ClaimingModeHandler {
    private boolean mouseTracking = false;

    @Getter
    private Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    private HashSet<XZ> chunks = new HashSet<>();

    public void onPreClick(FullscreenMapEvent.ClickEvent event) {
        if (!JMI.ftbchunks || !ClaimingMode.INSTANCE.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (!ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x(), xz.z()))) return;

        mouseTracking = true;
        addToWaitingList(xz);
        event.cancel();
    }

    public void onPreDrag(FullscreenMapEvent.MouseDraggedEvent event) {
        if (!JMI.ftbchunks || !ClaimingMode.INSTANCE.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (mouseTracking || ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x(), xz.z()))) event.cancel();
    }

    public void onMouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!JMI.ftbchunks || !mouseTracking) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (ClaimingMode.INSTANCE.getArea().contains(new ChunkPos(xz.x(), xz.z())) || chunks.contains(xz)) addToWaitingList(xz);
    }

    public void onMouseReleased(int mouseButton) {
        if (!JMI.ftbchunks || !mouseTracking || mouseButton > 1) return;

        applyChanges(mouseButton);
    }

    private void addToWaitingList(XZ xz) {
        final var polygon = ClaimingMode.INSTANCE.dragPolygon(xz);
        showOverlay(polygon);
        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private void applyChanges(int mouseButton) {
        mouseTracking = false;
        if (chunks.isEmpty()) return;

        new RequestChunkChangePacket(Screen.hasShiftDown() ? mouseButton+2 : mouseButton, chunks).sendToServer();
        removeOverlays(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F);
    }
}
