package me.frankv.jmi.api;

import journeymap.api.v2.client.IClientAPI;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory class that discovers, initializes, and manages compatibility modules.
 * <p>
 * This class uses Java's ServiceLoader to discover implementations of the {@link ModCompat} interface,
 * filters them based on whether their target mods are loaded, and initializes them with JourneyMap's API
 * and client configuration.
 */
@Slf4j
public class ModCompatFactory {
    private final Map<Class<? extends ModCompat>, ModCompat> modCompatMap;

    /**
     * Creates a new ModCompatFactory and initializes all discovered compatibility modules.
     * <p>
     * This constructor:
     * <ol>
     *   <li>Discovers all implementations of the {@link ModCompat} interface using ServiceLoader</li>
     *   <li>Filters out implementations where {@link ModCompat#isTargetModsLoaded()} returns false</li>
     *   <li>Initializes each compatibility module with JourneyMap's API and client configuration</li>
     *   <li>Registers event handlers for each compatibility module</li>
     *   <li>Subscribes to the AddButtonDisplay event to add buttons for toggleable overlays</li>
     * </ol>
     *
     * @param jmAPI The JourneyMap client API
     * @param clientConfig The client configuration
     * @param eventBus The JMI event bus
     */
    public ModCompatFactory(IClientAPI jmAPI, ClientConfig clientConfig, JMIEventBus eventBus) {
        modCompatMap = ServiceLoader.load(ModCompat.class).stream()
                .map(provider -> {
                    try {
                        return provider.get();
                    } catch (Exception | Error e) {
                        log.warn("Failed to init JMI mod compat {}", e.getLocalizedMessage(), e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(ModCompat::isTargetModsLoaded)
                .collect(Collectors.toUnmodifiableMap(ModCompat::getClass, Function.identity()));

        modCompatMap.values().forEach(compat -> {
            compat.init(jmAPI, clientConfig);
            compat.registerEvent(eventBus);
        });

        eventBus.subscribe(Event.AddButtonDisplay.class, this::onAddButtonDisplay);
    }

    /**
     * Gets a compatibility module by its class.
     *
     * @param clazz The class of the compatibility module to get
     * @param <T> The type of the compatibility module
     * @return The compatibility module, or null if not found
     */
    public <T extends ModCompat> T get(Class<T> clazz) {
        return Optional.ofNullable(modCompatMap.get(clazz))
                .map(clazz::cast)
                .orElse(null);
    }

    /**
     * Handles the AddButtonDisplay event by adding buttons for all toggleable overlays
     * provided by compatibility modules.
     *
     * @param event The AddButtonDisplay event
     */
    private void onAddButtonDisplay(Event.AddButtonDisplay event) {
        var themeButtonDisplay = event.themeButtonDisplay();

        modCompatMap.values().stream()
                .flatMap(modCompat -> modCompat.getToggleableOverlays().stream())
                .sorted(Comparator.comparing(ToggleableOverlay::getOrder))
                .forEach(t -> themeButtonDisplay.addThemeToggleButton(
                        t.getButtonLabel(),
                        t.getButtonLabel(),
                        t.getButtonIconName(),
                        t.isActivated(),
                        t::onToggle
                ));
    }

}
