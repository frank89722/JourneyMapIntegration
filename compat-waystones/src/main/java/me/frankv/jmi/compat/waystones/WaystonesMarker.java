package me.frankv.jmi.compat.waystones;

import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.util.OverlayHelper;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
public enum WaystonesMarker implements ToggleableOverlay {
    INSTANCE;

    private IClientAPI jmAPI;
    private final Minecraft mc = Minecraft.getInstance();

    private IClientConfig clientConfig;

    @Getter
    private boolean activated = true;

    @Getter
    private final String buttonLabel = "Waystones Overlay";
    @Getter
    private final int order = 2;

    private final HashMap<ComparableWaystone, MarkerOverlay> markers = new HashMap<>();
    @Getter
    private Set<ComparableWaystone> waystones = new HashSet<>();


    public void init(IClientAPI jmAPI, IClientConfig clientConfig) {
        this.jmAPI = jmAPI;
        this.clientConfig = clientConfig;
        Balm.getEvents().onEvent(WaystonesListReceivedEvent.class, this::onKnownWaystones);
    }

    private void createMarker(ComparableWaystone waystone) {
        final var marker = new ResourceLocation("jmi:images/waystone.png");
        final var icon = new MapImage(marker, 32, 32)
                .setAnchorX(12.0d)
                .setAnchorY(24.0d)
                .setDisplayWidth(24.0d)
                .setDisplayHeight(24.0d)
                .setColor(clientConfig.getWaystoneColor());

        final var textProperties = new TextProperties()
                .setBackgroundOpacity(0.4f)
                .setOpacity(1.0f);

        final var markerOverlay = new MarkerOverlay(Constants.MOD_ID, "waystone_" + waystone.pos, waystone.pos, icon);
        markerOverlay.setDimension(waystone.dim)
                .setLabel(waystone.name)
                .setTextProperties(textProperties);

        markerOverlay.setOverlayListener(new WaystonesMarkerListener(markerOverlay, jmAPI));

        markers.put(waystone, markerOverlay);

        if (activated) OverlayHelper.showOverlay(markerOverlay);
    }

    private void removeMarker(ComparableWaystone waystone) {
        if (!markers.containsKey(waystone)) return;

        try {
            jmAPI.remove(markers.remove(waystone));
            markers.remove(waystone);
        } catch (Exception e) {
            log.error(String.valueOf(e));
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

    public void onJMEvent(ClientEvent event) {
        switch (event.type) {
            case MAPPING_STARTED -> {
                createMarkersOnMappingStarted();
                log.debug("re-added waystones overlays");
            }

            case MAPPING_STOPPED -> markers.clear();
        }
    }

    public void onKnownWaystones(WaystonesListReceivedEvent event) {
        if (!clientConfig.getWaystone()) return;
        final var newWaystones = new HashSet<>(ComparableWaystone.fromEvent(event));
        final var oldWaystones = new HashSet<>(markers.keySet());

        //---------
        // WaystonesListReceivedEvent give a list with only a waystone in when there is a new waystone got placed. That why this exist
        if (newWaystones.size() == 1 && oldWaystones.size() > 2) return;
        //---------

        final var addWaystones = new HashSet<>(newWaystones);
        final var rmvWaystones = new HashSet<>(oldWaystones);

        rmvWaystones.removeAll(newWaystones);
        addWaystones.removeAll(oldWaystones);
        rmvWaystones.forEach(this::removeMarker);
        addWaystones.forEach(this::createMarker);

        waystones = newWaystones;

    }

    @Override
    public String getButtonIconName() {
        return "waypoints";
    }

    record ComparableWaystone(UUID uuid, String name, BlockPos pos, ResourceKey<Level> dim) {
        public static Set<ComparableWaystone> fromEvent(WaystonesListReceivedEvent event) {
            final var waystones = new HashSet<ComparableWaystone>();

            event.getWaystones().forEach(w -> {
                if (!w.hasName()) return;
                waystones.add(new ComparableWaystone(w.getWaystoneUid(), w.getName().tryCollapseToString(), w.getPos(), w.getDimension()));
            });

            return waystones;
        }
    }

}
