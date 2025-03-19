package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.model.TextProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class FTBChunksCompatStates {
    private final Map<ChunkDimPos, ClaimedChunk> chunkData = new HashMap<>();
    private final Map<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    private final Map<UUID, ShapeProperties> shapeProperties = new HashMap<>();
    private final Map<UUID, TextProperties> textProperties = new HashMap<>();
    private final Map<UUID, Set<PolygonWrapper>> teamOverlays = new HashMap<>();

    public void clearOverlays() {
        teamOverlays.clear();
        forceLoadedOverlays.clear();
    }

    public void resetData() {
        chunkData.clear();
        forceLoadedOverlays.clear();
        shapeProperties.clear();
        textProperties.clear();
        teamOverlays.clear();
    }

    public ShapeProperties getShapeProps(ClientTeam team, float opacity) {
        return shapeProperties.computeIfAbsent(team.getTeamId(), __ -> new ShapeProperties()
                .setStrokeWidth(1.5f)
                .setStrokeOpacity(.75f)
                .setStrokeColor(team.getColor())
                .setFillColor(team.getColor())
                .setFillOpacity(opacity));
    }

    public TextProperties getTextProps(ClientTeam team) {
        return textProperties.computeIfAbsent(team.getTeamId(), __ -> new TextProperties()
                .setBackgroundColor(team.getColor() << 2)
                .setMinZoom(250)
                .setFontShadow(true));
    }
}
