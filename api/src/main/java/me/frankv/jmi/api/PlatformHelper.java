package me.frankv.jmi.api;

import java.util.ServiceLoader;

/**
 * Interface for platform-specific functionality.
 * <p>
 * This interface provides methods for interacting with the underlying mod platform (Fabric, Forge, NeoForge).
 * It is implemented by platform-specific classes and loaded using Java's ServiceLoader mechanism.
 * <p>
 * The static {@link #PLATFORM} field provides access to the platform-specific implementation.
 */
public interface PlatformHelper {

    /**
     * Gets the name of the current platform
     *
     * @return The name of the current platform.
     */
    String getPlatformName();

    /**
     * Checks if a mod with the given id is loaded.
     *
     * @param modId The mod to check if it is loaded.
     * @return True if the mod is loaded, false otherwise.
     */
    boolean isModLoaded(String modId);

    /**
     * Check if the game is currently in a development environment.
     *
     * @return True if in a development environment, false otherwise.
     */
    boolean isDevelopmentEnvironment();

    /**
     * The platform-specific implementation of this interface.
     * <p>
     * This field is initialized using Java's ServiceLoader mechanism to find the appropriate
     * implementation for the current platform.
     */
    PlatformHelper PLATFORM = ServiceLoader.load(PlatformHelper.class).findFirst().orElseThrow();
}
