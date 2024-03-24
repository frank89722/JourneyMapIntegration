package me.frankv.jmi.api.event;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Slf4j
public class JMIEventBus {
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> eventHandlers = new HashMap<>();

    @Getter @Setter
    private boolean firstLogin = false;

    @Getter @Setter
    private boolean haveDim = false;


    @SuppressWarnings("unchecked")
    public <T extends Event> void sendEvent(T event) {
        Stream.ofNullable(eventHandlers.get(event.getClass()))
                .flatMap(Collection::stream)
                .forEach(consumer -> {
                    try {
                        ((Consumer<T>) consumer).accept(event);
                    } catch (ClassCastException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }

    public <T extends Event> void subscribe(Class<T> clazz, Consumer<T> consumer) {
        eventHandlers.computeIfAbsent(clazz, k -> new ArrayList<>()).add(consumer);
    }
}
