package frankv.jmi.waystones.client;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class WaystoneMarker {
    private IClientAPI jmAPI;
    public static HashMap<UUID, MarkerOverlay> markers = new HashMap<>();

    public WaystoneMarker(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
    }

    private void createMarker(IWaystone w) {
        ResourceLocation marker = new ResourceLocation("jmi:images/waystone.png");
        MapImage icon = new MapImage(marker, 32, 32)
                .setAnchorX(9.0d)
                .setAnchorY(18.0d)
                .setDisplayWidth(18.0d)
                .setDisplayHeight(18.0d)
                .setColor(JMI.CLIENT_CONFIG.getWaystoneColor());

        TextProperties textProperties = new TextProperties()
                .setMinZoom(2)
                .setOpacity(1.0f);

        MarkerOverlay markerOverlay = new MarkerOverlay(JMI.MODID, "waystone_" + w.getPos(), w.getPos(), icon);
        markerOverlay.setDimension(w.getDimension()).setTitle("Waystone")
                .setTitle(w.getName())
                .setTextProperties(textProperties);

        markerOverlay.setOverlayListener(new WaystoneMarkerListener(markerOverlay, jmAPI));

        try {
            jmAPI.show(markerOverlay);
            markers.put(w.getWaystoneUid(), markerOverlay);
        } catch (Exception e) {
            JMI.LOGGER.error(e);
        }
    }

    private void removeMarker(UUID uid) {
        if (!markers.containsKey(uid)) return;

        try {
            jmAPI.remove(markers.remove(uid));
            markers.remove(uid);
        } catch (Exception e) {
            JMI.LOGGER.error(e);
        }
    }

    @SubscribeEvent
    public void onKnownWaystones(KnownWaystonesEvent event) {
        if (!JMI.CLIENT_CONFIG.getWayStone()) return;
        List<IWaystone> newWaystones = new ArrayList<>(event.getWaystones());

        for (IWaystone o : newWaystones) {
            if (!o.hasName() || markers.containsKey(o.getWaystoneUid())) continue;
            createMarker(o);
        }

        for (Map.Entry<UUID, MarkerOverlay> e : new HashMap<>(markers).entrySet()) {
            UUID uid = e.getKey();
            boolean flag = false;
            for (IWaystone o : newWaystones) {
                if (!o.hasName()) continue;
                if (o.getWaystoneUid().equals(uid)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) removeMarker(uid);
        }
    }
}
