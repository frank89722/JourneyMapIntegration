package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import journeymap.api.v2.client.display.Overlay;
import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.ShapeProperties;
import journeymap.api.v2.client.model.TextProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.frankv.jmi.compat.ftbchunks.claimedchunksoverlay.TeamClaimGraph;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Getter
@NoArgsConstructor
public class FTBChunksCompatStates {
    private final Map<ChunkDimPos, ClaimedChunk> chunkData = new HashMap<>();
    private final Map<ChunkDimPos, Overlay> forceLoadedOverlays = new HashMap<>();
    private final Map<UUID, ShapeProperties> shapeProperties = new HashMap<>();
    private final Map<UUID, TextProperties> textProperties = new HashMap<>();
    private final Map<ResourceKey<Level>, Map<UUID, TeamClaims>> claims = new HashMap<>();
    private long generation;

    public TeamClaims teamClaims(ResourceKey<Level> dim, UUID teamId) {
        return claims.computeIfAbsent(dim, k -> new HashMap<>()).computeIfAbsent(teamId, k -> new TeamClaims());
    }

    public Optional<TeamClaims> findTeamClaims(ResourceKey<Level> dim, UUID teamId) {
        return Optional.ofNullable(claims.get(dim)).map(teams -> teams.get(teamId));
    }

    public Map<UUID, TeamClaims> claimsIn(ResourceKey<Level> dim) {
        return claims.getOrDefault(dim, Collections.emptyMap());
    }

    public Stream<PolygonOverlay> overlaysIn(ResourceKey<Level> dim) {
        return claimsIn(dim).values().stream().flatMap(TeamClaims::allOverlays);
    }

    public void clearForceLoaded() {
        forceLoadedOverlays.clear();
    }

    public void resetData() {
        chunkData.clear();
        forceLoadedOverlays.clear();
        shapeProperties.clear();
        textProperties.clear();
        claims.clear();
        generation++;
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
                .setColor(OverlayUtil.getTeamTextColor(team))
                .setMinZoom(250)
                .setFontShadow(true));
    }

    @Getter
    public static final class TeamClaims {
        private final TeamClaimGraph graph = new TeamClaimGraph();
        private final Map<Long, List<PolygonOverlay>> overlays = new HashMap<>();

        public Stream<PolygonOverlay> allOverlays() {
            return overlays.values().stream().flatMap(Collection::stream);
        }
    }
}
