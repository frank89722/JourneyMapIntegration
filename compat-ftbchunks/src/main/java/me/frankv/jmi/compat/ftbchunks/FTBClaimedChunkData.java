package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.model.TextProperties;
import journeymap.api.v2.client.util.PolygonHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.frankv.jmi.Constants;

import java.util.Date;
import java.util.UUID;

@Getter
@EqualsAndHashCode
public class FTBClaimedChunkData {

    private final ChunkSyncInfo chunk;
    private final ChunkDimPos chunkDimPos;
    private final boolean forceLoaded;
    private final UUID teamId;
    @EqualsAndHashCode.Exclude
    private ClientTeam team;
    @EqualsAndHashCode.Exclude
    private PolygonOverlay overlay;

    public FTBClaimedChunkData(MapDimension dim, ChunkSyncInfo chunk, UUID teamId) {
        long now = new Date().getTime();

        this.chunk = chunk;
        this.chunkDimPos = new ChunkDimPos(dim.dimension, chunk.x(), chunk.z());
        this.forceLoaded = chunk.getDateInfo(true, now).forceLoaded() != null;
        this.teamId = teamId;

        ClientTeamManagerImpl.getInstance().getTeam(teamId)
                .ifPresent(team -> {
                    this.team = team;
                    overlay = makeOverlay();
                });
    }

    private PolygonOverlay makeOverlay() {
        var color = team.getColor();
//        var displayId = "claimed_" + chunkDimPos.x() + ',' + chunkDimPos.z();
        var shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(color)
//                .setFillOpacity(JMI.clientConfig.getClaimedChunkOverlayOpacity().floatValue()); //TODO
                .setFillOpacity(0.25f);

        var textProps = new TextProperties()
                .setColor(color)
                .setOpacity(1f)
                .setFontShadow(true);

        var polygon = PolygonHelper.createChunkPolygon(chunkDimPos.x(), 1, chunkDimPos.z());

        var overlay = new PolygonOverlay(Constants.MOD_ID, chunkDimPos.dimension(), shapeProps, polygon);

        overlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(team.getDisplayName())
                .setTextProperties(textProps);

        return overlay;
    }

    void updateOverlayProps() {
        final var color = team.getColor();

        overlay.getTextProperties().setColor(color);
        overlay.getShapeProperties().setFillColor(color);
    }
}
