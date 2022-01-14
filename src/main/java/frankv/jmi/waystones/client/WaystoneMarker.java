package frankv.jmi.waystones.client;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.*;

public class WaystoneMarker {
    private IClientAPI jmAPI;
    public static HashMap<ComparableWaystone, MarkerOverlay> markers = new HashMap<>();

    public WaystoneMarker(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    private void createMarker(ComparableWaystone waystone) {
        var marker = new ResourceLocation("jmi:images/waystone.png");
        var icon = new MapImage(marker, 32, 32)
                .setAnchorX(12.0d)
                .setAnchorY(24.0d)
                .setDisplayWidth(24.0d)
                .setDisplayHeight(24.0d)
                .setColor(JMI.CLIENT_CONFIG.getWaystoneColor());

        var textProperties = new TextProperties()
                .setOpacity(1.0f);

        var markerOverlay = new MarkerOverlay(JMI.MODID, "waystone_" + waystone.pos, waystone.pos, icon);
        markerOverlay.setDimension(waystone.dim)
                .setTitle(waystone.name)
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

    public record ComparableWaystone(UUID uuid, String name, BlockPos pos, ResourceKey<Level> dim) {
        public static Set<ComparableWaystone> fromEvent(KnownWaystonesEvent event) {
            var waystones = new HashSet<ComparableWaystone>();

            for (var w : event.getWaystones()) {
                if (!w.hasName()) continue;
                waystones.add(new ComparableWaystone(w.getWaystoneUid(), w.getName(), w.getPos(), w.getDimension()));
            }

            return waystones;
        }
    }

    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (!JMI.CLIENT_CONFIG.getWayStone()) return;
        var newWaystones = new HashSet<>(ComparableWaystone.fromEvent(event));
        var oldWaystones = new HashSet<>(markers.keySet());

        //---------
        // KnownWaystonesEvent give a list with only a waystone in when there is a new waystone got placed. That why this exist
        if (newWaystones.size() == 1 && oldWaystones.size() > 2) return;
        //---------

        var addWaystones = new HashSet<>(newWaystones);
        var rmvWaystones = new HashSet<>(oldWaystones);

        rmvWaystones.removeAll(newWaystones);
        addWaystones.removeAll(oldWaystones);
        rmvWaystones.forEach(w -> removeMarker(w));
        addWaystones.forEach(w -> createMarker(w));

    }
}
