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
    public void onActivate(UIState mapState) {

    }

    @Override
    public void onDeactivate(UIState mapState) {

    }

    @Override
    public void onMouseMove(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {

    }

    @Override
    public void onMouseOut(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {

    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
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
    public void onOverlayMenuPopup(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, ModPopupMenu modPopupMenu) {

    }
}
