package me.frankv.jmi.api.jmoverlay;

import java.util.List;

public interface ClientConfig {

    Boolean getFtbChunks();

    Boolean getWaystone();

    List<? extends String> getWaypointMessageBlocks();

    Boolean getWaypointMessageEmptyHandOnly();

    Double getClaimedChunkOverlayOpacity();

    Boolean getDisableFTBFunction();

    Integer getWaystoneColor();

    Integer getDefaultConfigVersion();
}
