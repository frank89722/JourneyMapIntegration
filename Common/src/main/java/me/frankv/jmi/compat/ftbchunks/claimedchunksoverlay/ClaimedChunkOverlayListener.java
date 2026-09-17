package me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.api.v2.client.display.IOverlayListener;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.util.UIState;
import me.frankv.jmi.compat.ftbchunks.ClaimedChunk;
import me.frankv.jmi.compat.ftbchunks.FTBChunksCompatStates;
import me.frankv.jmi.compat.ftbchunks.OverlayUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ClaimedChunkOverlayListener(
        UUID teamId,
        FTBChunksCompatStates states,
        PolygonOverlay overlay
) implements IOverlayListener {

    @Override
    public void onMouseMove(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition) {
        var chunkDimPos = new ChunkDimPos(mapState.dimension, new ChunkPos(blockPosition));

        Optional.ofNullable(states.getChunkData().get(chunkDimPos))
                .flatMap(ClaimedChunk::getTeam)
                .map(ClientTeam::getTeamId)
                // Make sure the teamId we get from chunkDimPos is the same with the teamId of this overlay
                .filter(teamId -> Objects.equals(teamId, this.teamId))
                .map(teamId -> states.getTextProperties().get(teamId))
                .ifPresentOrElse(OverlayUtil::enableTextForTextProps,
                        () -> OverlayUtil.disableTextForTextProps(overlay.getTextProperties()));
    }

    @Override
    public boolean onMouseClick(UIState mapState, Point2D.Double mousePosition, BlockPos blockPosition, int button, boolean doubleClick) {
        return false;
    }
}
