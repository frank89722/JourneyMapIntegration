package me.frankv.jmi.compat.ftbchunks;

import journeymap.api.v2.client.display.PolygonOverlay;
import journeymap.api.v2.client.model.MapPolygon;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record PolygonWrapper(PolygonOverlay polygon, Level level) {
    private Set<Set<BlockPos>> makePosSet(List<MapPolygon> mapPolygons) {
        var result = new HashSet<Set<BlockPos>>();
        mapPolygons.forEach(o -> result.add((new HashSet<>(o.getPoints()))));
        return result;
    }

    private boolean compareHoles(List<MapPolygon> that) {
        if (that == null) return false;
        var holesSet = makePosSet(polygon.getHoles());
        var thatSet = makePosSet(that);
        return holesSet.equals(thatSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolygonWrapper that = (PolygonWrapper) o;
        return Objects.equals(polygon.getOuterArea().getPoints(), that.polygon.getOuterArea().getPoints()) && compareHoles(that.polygon.getHoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(polygon.getOuterArea().getPoints(), makePosSet(polygon.getHoles()));
    }
}
