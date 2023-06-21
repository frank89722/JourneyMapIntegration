package me.frankv.jmi.jmoverlay.ftbchunks;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;
import me.frankv.jmi.JMI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@EqualsAndHashCode
public class FTBClaimedChunkData {

    private final SendChunkPacket.SingleChunk chunk;
    private final ChunkDimPos chunkDimPos;
    private final boolean forceLoaded;
    private final UUID teamId;
    @EqualsAndHashCode.Exclude
    private ClientTeam team;
    @EqualsAndHashCode.Exclude
    private PolygonOverlay overlay;

    public FTBClaimedChunkData(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        this.chunk = chunk;
        this.chunkDimPos = new ChunkDimPos(dim.dimension, chunk.x, chunk.z);
        this.forceLoaded = chunk.forceLoaded;
        this.teamId = teamId;

        ClientTeamManagerImpl.getInstance().getTeam(teamId)
                .ifPresent(team -> {
                    this.team = team;
                    overlay = makeOverlay();
                });
    }

    private PolygonOverlay makeOverlay() {
        var color = team.getColor();
        var displayId = "claimed_" + chunkDimPos.x() + ',' + chunkDimPos.z();
        var shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(color)
                .setFillOpacity(JMI.clientConfig.getClaimedChunkOverlayOpacity().floatValue());

        var textProps = new TextProperties()
                .setColor(color)
                .setOpacity(1f)
                .setFontShadow(true);

        var polygon = PolygonHelper.createChunkPolygon(chunkDimPos.x(), 1, chunkDimPos.z());

        var overlay = new PolygonOverlay(JMI.MOD_ID, displayId, chunkDimPos.dimension(), shapeProps, polygon);

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
