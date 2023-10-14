package me.frankv.jmi.api;

import journeymap.client.api.IClientAPI;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModCompatFactory {
    private final Map<Class<? extends ModCompat>, ModCompat> modCompatMap;

    public ModCompatFactory(IClientAPI jmAPI, IClientConfig clientConfig, JMIEventBus eventBus) {
        modCompatMap = ServiceLoader.load(ModCompat.class).stream()
                .map(provider -> {
                    try {
                        return provider.get();
                    } catch (Error e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter(ModCompat::isTargetModsLoaded)
                .peek(compat -> {
                    compat.init(jmAPI, clientConfig);
                    compat.registerEvent(eventBus);
                })
                .collect(Collectors.toUnmodifiableMap(ModCompat::getClass, Function.identity()));

        eventBus.subscribe(Event.AddButtonDisplay.class, e ->
                modCompatMap.values().stream()
                        .flatMap(modCompat -> modCompat.getToggleableOverlays().stream())
                        .sorted(Comparator.comparing(ToggleableOverlay::getOrder))
                        .forEach(t -> {
                            var themeButtonDisplay = e.themeButtonDisplay();
                            themeButtonDisplay.addThemeToggleButton(t.getButtonLabel(), t.getButtonLabel(),
                                    t.getButtonIconName(), t.isActivated(), t::onToggle);
                 }));
    }

    public <T extends ModCompat> T get(Class<T> clazz) {
        return Optional.ofNullable(modCompatMap.get(clazz))
                .map(clazz::cast)
                .orElse(null);
    }

}
