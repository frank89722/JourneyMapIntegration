package me.frankv.jmi.api;

import journeymap.api.v2.client.IClientAPI;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.Set;

/**
 * Interface for compatibility modules that integrate other mods with JourneyMap.
 * <p>
 * Implementations of this interface are discovered using Java's ServiceLoader mechanism.
 * To register a compatibility module, create a file at:
 * {@code src/main/resources/META-INF/services/me.frankv.jmi.api.ModCompat}
 * containing the fully qualified name of your implementation class.
 */
public interface ModCompat {

    /**
     * Initializes the compatibility module with JourneyMap's API and client configuration.
     * This method is called when the mod is loaded and the target mod is present.
     *
     * @param jmAPI The JourneyMap client API
     * @param clientConfig The client configuration
     */
    void init(IClientAPI jmAPI, ClientConfig clientConfig);

    /**
     * Registers event handlers with the JMI event bus.
     * This method is called after initialization to set up event handling.
     *
     * @param eventBus The JMI event bus
     */
    void registerEvent(JMIEventBus eventBus);

    /**
     * Gets the set of toggleable overlays provided by this compatibility module.
     * These overlays will be displayed in JourneyMap's UI and can be toggled on/off.
     *
     * @return A set of toggleable overlays
     */
    Set<ToggleableOverlay> getToggleableOverlays();

    /**
     * Checks if this compatibility module is enabled in the configuration.
     *
     * @return true if the module is enabled, false otherwise
     */
    boolean isEnabled();

    /**
     * Checks if the target mods for this compatibility module are loaded.
     * This method is used to filter out compatibility modules for mods that aren't present.
     *
     * @return true if the target mods are loaded, false otherwise
     */
    boolean isTargetModsLoaded();
}
