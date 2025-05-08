package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.fullscreen.ModPopupMenu;
import journeymap.api.v2.client.util.UIState;
import me.frankv.jmi.compat.ftbchunks.ClaimedChunk;
import me.frankv.jmi.compat.ftbchunks.FTBChunksCompatStates;
import me.frankv.jmi.compat.ftbchunks.OverlayUtil;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;
import java.util.Optional;

public record ForceLoadedChunkOverlayListener(
        FTBChunksCompatStates states,
        ClaimedChunk data
) implements IOverlayListener {

    @Override
    public void onActivate(UIState mapState) {

    }

    @Override
    public void onDeactivate(UIState mapState) {

    }

    @Override
    public void onMouseMove(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {
        Optional.ofNullable(states.getTextProperties().get(data.teamId()))
                .ifPresent(OverlayUtil::disableTextForTextProps);

    }

    @Override
    public void onMouseOut(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {
        Optional.ofNullable(states.getTextProperties().get(data.teamId()))
                .ifPresent(OverlayUtil::enableTextForTextProps);
    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
        return false;
    }

    @Override
    public void onOverlayMenuPopup(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, ModPopupMenu modPopupMenu) {

    }
}

