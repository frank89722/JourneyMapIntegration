package me.frankv.jmi.compat.ftbchunks;

import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.fullscreen.ModPopupMenu;
import journeymap.api.v2.client.util.UIState;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

@Slf4j
public class TestLis implements IOverlayListener {
    @Override
    public void onActivate(UIState mapState) {
        log.info("onActivate");
    }

    @Override
    public void onDeactivate(UIState mapState) {

    }

    @Override
    public void onMouseMove(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {
        log.info("jwioefjiwef");
    }

    @Override
    public void onMouseOut(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {

    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
        log.info("jwioefjiwef123");
        return false;
    }

    @Override
    public void onOverlayMenuPopup(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, ModPopupMenu modPopupMenu) {

    }
}
