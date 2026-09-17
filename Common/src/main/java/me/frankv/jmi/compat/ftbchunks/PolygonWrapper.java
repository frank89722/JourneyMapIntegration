package me.frankv.jmi.compat.ftbchunks;

import journeymap.api.v2.client.display.PolygonOverlay;

import java.util.Objects;

public record PolygonWrapper(PolygonOverlay polygon) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PolygonWrapper that = (PolygonWrapper) o;
        return Objects.equals(polygon.getOuterArea().getPoints(), that.polygon.getOuterArea().getPoints())
                && Objects.equals(polygon.getHoles(), that.polygon.getHoles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(polygon.getOuterArea().getPoints(), polygon.getHoles());
    }
}
