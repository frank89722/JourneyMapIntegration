package frankv.jmi.jmoverlay.ftbchunks;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;
import frankv.jmi.JMI;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.model.ShapeProperties;
import journeymap.client.api.model.TextProperties;
import journeymap.client.api.util.PolygonHelper;

import java.util.Objects;
import java.util.UUID;

public class FTBClaimedChunkData {

    public final SendChunkPacket.SingleChunk chunk;
    public final ChunkDimPos chunkDimPos;
    public final boolean forceLoaded;
    public final UUID teamId;
    public final ClientTeam team;
    public final PolygonOverlay overlay;

    public FTBClaimedChunkData(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        this.chunk = chunk;
        this.chunkDimPos = new ChunkDimPos(dim.dimension, chunk.x, chunk.z);
        this.forceLoaded = chunk.forceLoaded;
        this.teamId = teamId;
        this.team = ClientTeamManager.INSTANCE.getTeam(teamId);

        if (team == null) this.overlay = null;
        else this.overlay = makeOverlay();
    }

    private PolygonOverlay makeOverlay() {
        var color = team.getColor();
        var displayId = "claimed_" + chunkDimPos.x + ',' + chunkDimPos.z;
        var shapeProps = new ShapeProperties()
                .setStrokeWidth(0)
                .setFillColor(color)
                .setFillOpacity(JMI.clientConfig.getClaimedChunkOverlayOpacity().floatValue());

        var textProps = new TextProperties()
                .setColor(color)
                .setOpacity(1f)
                .setFontShadow(true);

        var polygon = PolygonHelper.createChunkPolygon(chunkDimPos.x, 1, chunkDimPos.z);

        var overlay = new PolygonOverlay(JMI.MOD_ID, displayId, chunkDimPos.dimension, shapeProps, polygon);

        overlay.setOverlayGroupName("Claimed Chunks")
                .setTitle(team.getDisplayName())
                .setTextProperties(textProps);

        return overlay;
    }

    public void updateOverlayProps() {
        final var color = team.getColor();

        overlay.getTextProperties().setColor(color);
        overlay.getShapeProperties().setFillColor(color);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FTBClaimedChunkData chunkData = (FTBClaimedChunkData) o;
        return forceLoaded == chunkData.forceLoaded && Objects.equals(chunk, chunkData.chunk) && Objects.equals(chunkDimPos, chunkData.chunkDimPos) && Objects.equals(teamId, chunkData.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, chunkDimPos, forceLoaded, teamId);
    }
}
