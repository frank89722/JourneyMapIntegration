package me.frankv.jmi.api.event;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Event bus for the JourneyMap Integration mod.
 * <p>
 * This class provides a unified event dispatching system that works across all supported mod platforms
 * (Fabric, Forge, NeoForge). It allows components to subscribe to specific event types and receive
 * notifications when those events occur.
 * <p>
 * Platform-specific event adapters convert platform-specific events to JMI's internal event format
 * and send them to this bus, which then dispatches them to all registered handlers.
 */
@Slf4j
public class JMIEventBus {
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> eventHandlers = new HashMap<>();

    /**
     * Indicates whether this is the first login.
     * <p>
     * This flag is used to determine whether certain initialization tasks should be performed.
     */
    @Getter @Setter
    private boolean firstLogin = false;

    /**
     * Indicates whether dimension information is available.
     * <p>
     * This flag is used to determine whether certain dimension-dependent operations can be performed.
     */
    @Getter @Setter
    private boolean haveDim = false;

    /**
     * Sends an event to all subscribed handlers.
     * <p>
     * This method dispatches the given event to all handlers that have subscribed to its type.
     *
     * @param event The event to send
     * @param <T> The type of the event
     */
    @SuppressWarnings("unchecked")
    public <T extends Event> void sendEvent(T event) {
        Optional.ofNullable(eventHandlers.get(event.getClass()))
                .orElseGet(ArrayList::new)
                .forEach(consumer -> ((Consumer<T>) consumer).accept(event));
    }

    /**
     * Subscribes a handler to a specific event type.
     * <p>
     * This method registers a consumer function to be called when events of the specified type are sent.
     *
     * @param clazz The class of the event type to subscribe to
     * @param consumer The consumer function to call when events of the specified type are sent
     * @param <T> The type of the event
     */
    public <T extends Event> void subscribe(Class<T> clazz, Consumer<T> consumer) {
        eventHandlers.computeIfAbsent(clazz, k -> new ArrayList<>()).add(consumer);
    }
}
