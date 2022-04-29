package frankv.jmi.config;

import java.util.List;

public interface IClientConfig {

    Boolean getFtbChunks();

    Boolean getWaystone();

    List<? extends String> getWaypointMessageBlocks();

    Boolean getWaypointMessageEmptyHandOnly();

    Double getClaimedChunkOverlayOpacity();

    Boolean getDisableFTBFunction();

    Boolean getShowClaimChunkScreen();

    Integer getWaystoneColor();

    Integer getDefaultConfigVersion();
}
