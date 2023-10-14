package me.frankv.jmi.compat.waystones;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.util.UIState;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

@Slf4j
public class WaystonesMarkerListener implements IOverlayListener {
    final MarkerOverlay overlay;
    final IClientAPI jmAPI;

    public WaystonesMarkerListener(MarkerOverlay overlay, IClientAPI jmAPI) {
        this.overlay = overlay;
        this.jmAPI = jmAPI;
    }

    @Override
    public void onActivate(UIState uiState) {

    }

    @Override
    public void onDeactivate(UIState uiState) {

    }

    @Override
    public void onMouseMove(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {

    }

    @Override
    public void onMouseOut(UIState uiState, Point2D.Double aDouble, BlockPos blockPos) {

    }

    @Override
    public boolean onMouseClick(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, int i, boolean b) {
        Waypoint waypoint = new Waypoint(
                Constants.MOD_ID,
                String.format("waystone_%s_%s_%s", overlay.getTitle(), overlay.getDimension(), overlay.getPoint()),
                overlay.getLabel(), overlay.getDimension(), overlay.getPoint()
        );

        try {
            jmAPI.show(waypoint);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public void onOverlayMenuPopup(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, ModPopupMenu modPopupMenu) {

    }
}
