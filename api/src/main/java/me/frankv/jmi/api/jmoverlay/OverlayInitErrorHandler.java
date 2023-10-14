package me.frankv.jmi.api.jmoverlay;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class OverlayInitErrorHandler {
    public static final Map<String, Runnable> handlers = new HashMap<>();

    public static <T extends ToggleableOverlay> void handle(Class<T> clazz) {
        Optional.ofNullable(handlers.get(clazz.getTypeName())).ifPresent(Runnable::run);
    }

}
