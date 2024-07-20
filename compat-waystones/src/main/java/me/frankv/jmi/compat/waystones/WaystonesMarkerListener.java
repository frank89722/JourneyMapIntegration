package me.frankv.jmi.compat.waystones;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.fullscreen.ModPopupMenu;
import journeymap.api.v2.client.util.UIState;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
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
        Waypoint waypoint = WaypointFactory.createClientWaypoint(
                Constants.MOD_ID,
                blockPos,
                String.format("waystone_%s_%s_%s", overlay.getTitle(), overlay.getDimension(), overlay.getPoint()),
                overlay.getDimension(),
                true
        );
//        Waypoint waypoint = new Waypoint(
//                Constants.MOD_ID,
//                String.format("waystone_%s_%s_%s", overlay.getTitle(), overlay.getDimension(), overlay.getPoint()),
//                overlay.getLabel(), overlay.getDimension(), overlay.getPoint()
//        );

        try {
            jmAPI.addWaypoint(Constants.MOD_ID, waypoint);
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
