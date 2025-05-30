package me.frankv.jmi.compat.waystones;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.display.MarkerOverlay;
import journeymap.api.v2.client.fullscreen.IThemeButton;
import journeymap.api.v2.client.model.MapImage;
import journeymap.api.v2.client.model.TextProperties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.util.OverlayHelper;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.event.WaystonesListReceivedEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

@Slf4j
public enum WaystonesMarker implements ToggleableOverlay {
    INSTANCE;

    private final Minecraft mc = Minecraft.getInstance();
    @Getter
    private final String buttonLabel = "jmi.toggleable_overlay.waystones";
    @Getter
    private final int order = 2;
    @Getter
    private final Map<ResourceLocation, Set<WaystoneMeta>> waystones = new HashMap<>();
    private final HashMap<WaystoneMeta, MarkerOverlay> markers = new HashMap<>();
    private IClientAPI jmAPI;
    private ClientConfig clientConfig;
    @Getter
    private boolean activated = true;

    public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
        this.jmAPI = jmAPI;
        this.clientConfig = clientConfig;
        Balm.getEvents().onEvent(WaystonesListReceivedEvent.class, this::onWaystonesListReceived);
    }

    private void createMarker(WaystoneMeta waystone) {
        final var marker = ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "images/waystone.png");
        final var icon = new MapImage(marker, 32, 32)
                .setAnchorX(16.0d)
                .setAnchorY(38.0d)
                .setDisplayWidth(32.0d)
                .setDisplayHeight(32.0d)
                .setColor(clientConfig.getWaystoneColor());

        final var textProperties = new TextProperties()
                .setBackgroundOpacity(0.4f)
                .setOpacity(1.0f);

        final var markerOverlay = new MarkerOverlay(Constants.MOD_ID, waystone.pos, icon);
        markerOverlay.setDimension(waystone.dim)
                .setLabel(waystone.name)
                .setTextProperties(textProperties);

        markerOverlay.setOverlayListener(new WaystonesMarkerListener(markerOverlay, jmAPI));

        markers.put(waystone, markerOverlay);

        if (activated) OverlayHelper.showOverlay(markerOverlay);
    }

    private void removeMarker(WaystoneMeta waystone) {
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

        waystones.values().stream()
                .flatMap(Collection::stream)
                .filter(waystoneMeta -> waystoneMeta.dim.equals(level.dimension()))
                .forEach(this::createMarker);
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

    public void onJMMapping(Event.JMMappingEvent event) {
        switch (event.mappingEvent().getStage()) {
            case MAPPING_STARTED -> {
                if (!event.firstLogin()) {
                    createMarkersOnMappingStarted();
                }
                log.debug("re-added waystones overlays");
            }

            case MAPPING_STOPPED -> markers.clear();
        }
    }

    public void onWaystonesListReceived(WaystonesListReceivedEvent event) {
        if (!clientConfig.getWaystone()) return;

        var oldWaystones = Optional.ofNullable(waystones.get(event.getWaystoneType())).orElseGet(HashSet::new);
        var newWaystones = new HashSet<>(WaystoneMeta.fromEvent(event));

        final var addWaystones = new HashSet<>(newWaystones);
        final var rmvWaystones = new HashSet<>(oldWaystones);

        rmvWaystones.removeAll(newWaystones);
        addWaystones.removeAll(oldWaystones);
        rmvWaystones.forEach(this::removeMarker);
        addWaystones.forEach(this::createMarker);

        waystones.put(event.getWaystoneType(), newWaystones);
    }

    @Override
    public ResourceLocation getButtonIconName() {
        return OverlayHelper.getIcon("waypoints");
    }


    record WaystoneMeta(UUID uuid, String name, BlockPos pos, ResourceKey<Level> dim) {
        public static Set<WaystoneMeta> fromEvent(WaystonesListReceivedEvent event) {
            final var waystones = new HashSet<WaystoneMeta>();

            event.getWaystones().forEach(w -> {
                if (!w.hasName()) return;
                waystones.add(new WaystoneMeta(w.getWaystoneUid(), w.getName().tryCollapseToString(), w.getPos(), w.getDimension()));
            });

            return waystones;
        }
    }

}
