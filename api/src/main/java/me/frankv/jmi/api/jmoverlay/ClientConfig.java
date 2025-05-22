package me.frankv.jmi.api.jmoverlay;

import java.util.List;

/**
 * Interface for accessing client configuration settings.
 * <p>
 * This interface provides methods to access various configuration settings that control
 * the behavior of JourneyMap Integration and its compatibility modules.
 * <p>
 * Platform-specific implementations of this interface are provided for each supported
 * mod platform (Fabric, Forge, NeoForge).
 */
public interface ClientConfig {

    /**
     * Gets whether FTB Chunks integration is enabled.
     *
     * @return true if FTB Chunks integration is enabled, false otherwise
     */
    Boolean getFtbChunks();

    /**
     * Gets whether Waystones integration is enabled.
     *
     * @return true if Waystones integration is enabled, false otherwise
     */
    Boolean getWaystone();

    /**
     * Gets the list of block IDs that should trigger waypoint messages when interacted with.
     *
     * @return a list of block IDs
     */
    List<? extends String> getWaypointMessageBlocks();

    /**
     * Gets whether waypoint messages should only be shown when the player has an empty hand.
     *
     * @return true if waypoint messages should only be shown with an empty hand, false otherwise
     */
    Boolean getWaypointMessageEmptyHandOnly();

    /**
     * Gets the opacity of the claimed chunk overlay.
     *
     * @return the opacity value (0.0 to 1.0)
     */
    Double getClaimedChunkOverlayOpacity();

    /**
     * Gets whether FTB Chunks functions that conflict with JourneyMap should be disabled.
     *
     * @return true if conflicting FTB Chunks functions should be disabled, false otherwise
     */
    Boolean getDisableFTBFunction();

    /**
     * Gets the color to use for waystone markers on the map.
     *
     * @return the color as an RGB integer
     */
    Integer getWaystoneColor();

    /**
     * Gets the default configuration version.
     *
     * @return the default configuration version
     */
    Integer getDefaultConfigVersion();
}
