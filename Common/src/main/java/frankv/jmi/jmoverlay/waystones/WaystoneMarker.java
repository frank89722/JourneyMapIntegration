package frankv.jmi.jmoverlay.waystones;

import frankv.jmi.JMI;
import frankv.jmi.jmoverlay.JMOverlayManager;
import frankv.jmi.jmoverlay.ToggleableOverlay;
import frankv.jmi.util.OverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import lombok.Getter;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public enum WaystoneMarker implements ToggleableOverlay {
    INSTANCE;

    private IClientAPI jmAPI;
    private Minecraft mc = Minecraft.getInstance();

    @Getter
    private boolean activated = true;

    @Getter
    private final String buttonLabel = "Waystones Overlay";
    @Getter
    private final int order = 2;

    private HashMap<ComparableWaystone, MarkerOverlay> markers = new HashMap<>();
    @Getter
    private Set<ComparableWaystone> waystones = new HashSet<>();

    WaystoneMarker() {
        JMOverlayManager.INSTANCE.registerOverlay(this);
    }

    @Override
    public void init(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    private void createMarker(ComparableWaystone waystone) {
        final var marker = new ResourceLocation("jmi:images/waystone.png");
        final var icon = new MapImage(marker, 32, 32)
                .setAnchorX(12.0d)
                .setAnchorY(24.0d)
                .setDisplayWidth(24.0d)
                .setDisplayHeight(24.0d)
                .setColor(JMI.clientConfig.getWaystoneColor());

        final var textProperties = new TextProperties()
                .setBackgroundOpacity(0.4f)
                .setOpacity(1.0f);

        final var markerOverlay = new MarkerOverlay(JMI.MOD_ID, "waystone_" + waystone.pos, waystone.pos, icon);
        markerOverlay.setDimension(waystone.dim)
                .setLabel(waystone.name)
                .setTextProperties(textProperties);

        markerOverlay.setOverlayListener(new WaystoneMarkerListener(markerOverlay, jmAPI));

        markers.put(waystone, markerOverlay);

        if (activated) OverlayHelper.showOverlay(markerOverlay);
    }

    private void removeMarker(ComparableWaystone waystone) {
        if (!markers.containsKey(waystone)) return;

        try {
            jmAPI.remove(markers.remove(waystone));
            markers.remove(waystone);
        } catch (Exception e) {
            JMI.LOGGER.error(String.valueOf(e));
        }
    }

    private void createMarkersOnMappingStarted() {
        final var level = mc.level;
        if (level == null) return;

        for (var data : waystones) {
            if (data.dim.equals(level.dimension())) createMarker(data);
        }
    }

    @Override
    public void onToggle(IThemeButton button) {
        if (activated) {
            OverlayHelper.removeOverlays(markers.values());
        } else {
            OverlayHelper.showOverlays(markers.values());
        }
        activated = !activated;
        button.setToggled(activated);
    }

    @Override
    public void onJMEvent(ClientEvent event) {
        if (!isEnabled()) return;

        switch (event.type) {
            case MAPPING_STARTED -> {
                createMarkersOnMappingStarted();
                JMI.LOGGER.debug("re-added waystones overlays");
            }

            case MAPPING_STOPPED -> markers.clear();
        }
    }

    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (!JMI.clientConfig.getWaystone()) return;
        final var newWaystones = new HashSet<>(ComparableWaystone.fromEvent(event));
        final var oldWaystones = new HashSet<>(markers.keySet());

        //---------
        // KnownWaystonesEvent give a list with only a waystone in when there is a new waystone got placed. That why this exist
        if (newWaystones.size() == 1 && oldWaystones.size() > 2) return;
        //---------

        final var addWaystones = new HashSet<>(newWaystones);
        final var rmvWaystones = new HashSet<>(oldWaystones);

        rmvWaystones.removeAll(newWaystones);
        addWaystones.removeAll(oldWaystones);
        rmvWaystones.forEach(this::removeMarker);
        addWaystones.forEach(this::createMarker);

        waystones = (Set<ComparableWaystone>) newWaystones.clone();

    }

    @Override
    public boolean isEnabled() {
        return JMI.waystones;
    }

    @Override
    public String getButtonIconName() {
        return "waypoints";
    }

    record ComparableWaystone(UUID uuid, String name, BlockPos pos, ResourceKey<Level> dim) {
        public static Set<ComparableWaystone> fromEvent(KnownWaystonesEvent event) {
            final var waystones = new HashSet<ComparableWaystone>();

            event.getWaystones().forEach(w -> {
                if (!w.hasName()) return;
                waystones.add(new ComparableWaystone(w.getWaystoneUid(), w.getName(), w.getPos(), w.getDimension()));
            });

            return waystones;
        }
    }
}
