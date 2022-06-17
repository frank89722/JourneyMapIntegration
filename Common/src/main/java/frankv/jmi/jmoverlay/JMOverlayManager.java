package frankv.jmi.jmoverlay;

import journeymap.client.api.IClientAPI;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
public class JMOverlayManager {
    public static final JMOverlayManager INSTANCE = new JMOverlayManager();

    @Setter
    private IClientAPI jmAPI;

    @Getter
    private Map<Class<? extends ToggleableOverlay>, ToggleableOverlay> toggleableOverlays = new HashMap<>();

    public void registerOverlay(@NotNull ToggleableOverlay overlay) {
        overlay.init(jmAPI);
        toggleableOverlays.put(overlay.getClass(), overlay);
    }
}
