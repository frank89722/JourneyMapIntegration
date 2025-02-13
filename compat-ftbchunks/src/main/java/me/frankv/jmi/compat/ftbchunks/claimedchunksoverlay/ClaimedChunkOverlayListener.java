package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.fullscreen.ModPopupMenu;
import journeymap.api.v2.client.util.UIState;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.compat.ftbchunks.FTBChunksCompatStates;
import net.minecraft.core.BlockPos;

import java.awt.geom.Point2D;

@Slf4j
public record ClaimedChunkOverlayListener(FTBChunksCompatStates states,
                                          PolygonOverlay overlay) implements IOverlayListener {

    @Override
    public void onActivate(UIState mapState) {

    }

    @Override
    public void onDeactivate(UIState mapState) {

    }

    @Override
    public void onMouseMove(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {
//        var chunkDimPos = new ChunkDimPos(mapState.dimension, new ChunkPos(blockPosition));
//
//        var teamId = Optional.ofNullable(states.getChunkData().get(chunkDimPos))
//                .flatMap(FTBClaimedChunkData::getTeam)
//                .map(ClientTeam::getTeamId)
//                .orElse(null);
//
//        Optional.ofNullable(states.getTextProperties().get(teamId))
//                .ifPresentOrElse(o -> o.setMinZoom(250),
//                        () -> overlay.getTextProperties().setMinZoom(Integer.MAX_VALUE));
    }

    @Override
    public void onMouseOut(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {

    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
        return false;
    }

    @Override
    public void onOverlayMenuPopup(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, ModPopupMenu modPopupMenu) {

    }
}
