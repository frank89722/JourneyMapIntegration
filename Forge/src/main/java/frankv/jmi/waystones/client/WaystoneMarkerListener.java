package frankv.jmi.waystones.client;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IOverlayListener;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.display.ModPopupMenu;
import journeymap.client.api.display.Waypoint;
import journeymap.client.api.util.UIState;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

public class WaystoneMarkerListener implements IOverlayListener {
    final MarkerOverlay overlay;
    final IClientAPI jmAPI;

    public WaystoneMarkerListener(MarkerOverlay overlay, IClientAPI jmAPI) {
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
        Waypoint waypoint = new Waypoint(JMI.MODID, String.format("waystone_%s_%s_%s", overlay.getTitle(), overlay.getDimension(), overlay.getPoint()), overlay.getTitle(), overlay.getDimension(), overlay.getPoint());

        try {
            jmAPI.show(waypoint);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onOverlayMenuPopup(UIState uiState, Point2D.Double aDouble, BlockPos blockPos, ModPopupMenu modPopupMenu) {

    }
}
