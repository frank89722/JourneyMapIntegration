package me.frankv.jmi.compat.ftbchunks;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.event.FullscreenMapEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static me.frankv.jmi.util.OverlayHelper.removeOverlays;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;

@RequiredArgsConstructor
public class ClaimingModeHandler {
    private boolean mouseTracking = false;
    private final ClaimingMode claimingMode;

    @Getter
    private final Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    private final HashSet<XZ> chunks = new HashSet<>();

    public void onClick(FullscreenMapEvent.ClickEvent event) {
        if (event.getStage() != FullscreenMapEvent.Stage.PRE) return;
        if (!claimingMode.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (!claimingMode.getArea().contains(new ChunkPos(xz.x(), xz.z()))) return;

        mouseTracking = true;
        addToWaitingList(xz);
        event.cancel();
    }

    public void onDrag(FullscreenMapEvent.MouseDraggedEvent event) {
        if (event.getStage() != FullscreenMapEvent.Stage.PRE) return;
        if (!claimingMode.isActivated()) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (mouseTracking || claimingMode.getArea().contains(new ChunkPos(xz.x(), xz.z()))) event.cancel();
    }

    public void onMouseMove(FullscreenMapEvent.MouseMoveEvent event) {
        if (!mouseTracking) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (claimingMode.getArea().contains(new ChunkPos(xz.x(), xz.z())) && !chunks.contains(xz)) addToWaitingList(xz);
    }

    public void onMouseReleased(int mouseButton) {
        if (!mouseTracking || mouseButton > 1) return;

        applyChanges(mouseButton);
    }

    private void addToWaitingList(XZ xz) {
        final var polygon = claimingMode.dragPolygon(xz);
        showOverlay(polygon);
        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private void applyChanges(int mouseButton) {
        mouseTracking = false;
        if (chunks.isEmpty()) return;
        var chunkChangeOp = RequestChunkChangePacket.ChunkChangeOp.create(mouseButton == 0, Screen.hasShiftDown());
        var packet = new RequestChunkChangePacket(chunkChangeOp, chunks);
        NetworkManager.sendToServer(packet);
        removeOverlays(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F);
    }
}
