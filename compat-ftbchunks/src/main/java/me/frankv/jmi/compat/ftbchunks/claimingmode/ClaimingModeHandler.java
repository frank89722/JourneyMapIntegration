package me.frankv.jmi.compat.ftbchunks.claimingmode;

import dev.architectury.networking.NetworkManager;
import dev.ftb.mods.ftbchunks.net.RequestChunkChangePacket;
import dev.ftb.mods.ftblibrary.math.XZ;
import dev.ftb.mods.ftblibrary.ui.GuiHelper;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.event.FullscreenMapEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.ChunkPos;

import java.util.*;

import static me.frankv.jmi.util.OverlayHelper.removeOverlays;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;

@RequiredArgsConstructor
public class ClaimingModeHandler {
    private static final List<Integer> ALLOWED_BUTTONS = List.of(0, 1);

    private final ClaimingMode claimingMode;
    @Getter
    private final Map<XZ, PolygonOverlay> dragPolygons = new HashMap<>();
    private final Set<XZ> chunks = new HashSet<>();
    private boolean mouseTracking = false;
    private Integer mouseButton = null;

    public void onClick(FullscreenMapEvent.ClickEvent event) {
        if (mouseTracking) {
            event.cancel();
            return;
        }
        if (!claimingMode.isActivated()) return;
        if (!ALLOWED_BUTTONS.contains(event.getButton())) return;
        if (event.getStage() != FullscreenMapEvent.Stage.PRE) return;

        final var xz = XZ.chunkFromBlock(event.getLocation().getX(), event.getLocation().getZ());
        if (!claimingMode.getArea().contains(new ChunkPos(xz.x(), xz.z()))) return;

        mouseTracking = true;
        mouseButton = event.getButton();
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

    public void onMouseReleased() {
        if (!mouseTracking) return;
        applyChanges();
    }

    private void addToWaitingList(XZ xz) {
        final var polygon = claimingMode.dragPolygon(xz);
        showOverlay(polygon);
        dragPolygons.put(xz, polygon);
        chunks.add(xz);
    }

    private void applyChanges() {
        var chunkChangeOp = RequestChunkChangePacket.ChunkChangeOp.create(mouseButton == 0, Screen.hasShiftDown());
        var packet = new RequestChunkChangePacket(chunkChangeOp, chunks, false, Optional.empty());
        NetworkManager.sendToServer(packet);
        GuiHelper.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 1.0F);
        clearStates();
    }

    void clearStates() {
        mouseTracking = false;
        removeOverlays(dragPolygons.values());
        chunks.clear();
        dragPolygons.clear();
        mouseButton = null;
    }
}
