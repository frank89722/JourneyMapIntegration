package me.frankv.jmi.api.event;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class JMIEventBus {
    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> eventHandlers = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T extends Event> void sendEvent(T event) {
        Optional.ofNullable(eventHandlers.get(event.getClass()))
                .orElseGet(ArrayList::new)
                .forEach(consumer -> {
                    try {
                        ((Consumer<T>) consumer).accept(event);
                    } catch (Throwable t) {
                        log.error("Error handling {}", event.getClass().getSimpleName(), t);
                    }
                });
    }

    public <T extends Event> void subscribe(Class<T> clazz, Consumer<T> consumer) {
        eventHandlers.computeIfAbsent(clazz, k -> new ArrayList<>()).add(consumer);
    }

}
