package frankv.jmi.waystones.client;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class WaystoneMarker {
    private IClientAPI jmAPI;
    private static Minecraft mc = Minecraft.getInstance();
    public static HashMap<ComparableWaystone, MarkerOverlay> markers = new HashMap<>();
    public static Set<ComparableWaystone> waystones = new HashSet<>();

    public WaystoneMarker(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    private void createMarker(ComparableWaystone waystone) {
        ResourceLocation marker = new ResourceLocation("jmi:images/waystone.png");
        MapImage icon = new MapImage(marker, 32, 32)
                .setAnchorX(12.0d)
                .setAnchorY(24.0d)
                .setDisplayWidth(24.0d)
                .setDisplayHeight(24.0d)
                .setColor(JMI.CLIENT_CONFIG.getWaystoneColor());

        TextProperties textProperties = new TextProperties()
                .setOpacity(1.0f);

        MarkerOverlay markerOverlay = new MarkerOverlay(JMI.MODID, "waystone_" + waystone.pos, waystone.pos, icon);
        markerOverlay.setDimension(waystone.dim)
                .setLabel(waystone.name)
                .setTextProperties(textProperties);

        markerOverlay.setOverlayListener(new WaystoneMarkerListener(markerOverlay, jmAPI));

        try {
            jmAPI.show(markerOverlay);
            markers.put(waystone, markerOverlay);
        } catch (Exception e) {
            JMI.LOGGER.error(e);
        }
    }

    private void removeMarker(ComparableWaystone waystone) {
        if (!markers.containsKey(waystone)) return;

        try {
            jmAPI.remove(markers.remove(waystone));
            markers.remove(waystone);
        } catch (Exception e) {
            JMI.LOGGER.error(e);
        }
    }

    public void createMarkersOnMappingStarted() {
        World level = mc.level;
        if (level == null) return;

        for (ComparableWaystone data : waystones) {
            if (data.dim.equals(level.dimension())) createMarker(data);
        }
    }

    @SubscribeEvent
    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (!JMI.CLIENT_CONFIG.getWayStone()) return;
        Set<ComparableWaystone> newWaystones = new HashSet<>(ComparableWaystone.fromEvent(event));
        Set<ComparableWaystone> oldWaystones = new HashSet<>(markers.keySet());

        //---------
        // KnownWaystonesEvent give a list with only a waystone in when there is a new waystone got placed. That why this exist
        if (newWaystones.size() == 1 && oldWaystones.size() > 2) return;
        //---------

        Set<ComparableWaystone> addWaystones = new HashSet<>(newWaystones);
        Set<ComparableWaystone> rmvWaystones = new HashSet<>(oldWaystones);

        rmvWaystones.removeAll(newWaystones);
        addWaystones.removeAll(oldWaystones);
        rmvWaystones.forEach(this::removeMarker);
        addWaystones.forEach(this::createMarker);

        waystones = (Set<ComparableWaystone>) ((HashSet<ComparableWaystone>) newWaystones).clone();
    }
}
