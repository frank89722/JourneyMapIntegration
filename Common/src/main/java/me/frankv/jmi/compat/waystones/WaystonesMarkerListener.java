package me.frankv.jmi.compat.waystones;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.util.UIState;
import journeymap.api.v2.common.waypoint.Waypoint;
import journeymap.api.v2.common.waypoint.WaypointFactory;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

@Slf4j
public class WaystonesMarkerListener implements IOverlayListener {
    private final MarkerOverlay overlay;
    private final IClientAPI jmAPI;

    public WaystonesMarkerListener(MarkerOverlay overlay, IClientAPI jmAPI) {
        this.overlay = overlay;
        this.jmAPI = jmAPI;
    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
        if (button == 1) {
            return true;
        }

        var alreadyCreated = jmAPI.getWaypoints(Constants.MOD_ID).stream()
                .filter(w -> w.getBlockPos().equals(overlay.getPoint()))
                .anyMatch(w -> w.getName().equals(overlay.getLabel()));

        if (alreadyCreated) {
            return false;
        }

        Waypoint waypoint = WaypointFactory.createWaypoint(
                Constants.MOD_ID,
                overlay.getPoint(),
                overlay.getLabel(),
                overlay.getDimension(),
                true
        );

        try {
            jmAPI.addWaypoint(Constants.MOD_ID, waypoint);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

}
