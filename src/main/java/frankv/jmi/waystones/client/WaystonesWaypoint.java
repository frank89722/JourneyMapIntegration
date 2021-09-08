package frankv.jmi.waystones.client;

import frankv.jmi.JMI;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.MarkerOverlay;
import journeymap.client.api.model.MapImage;
import journeymap.client.api.model.TextProperties;
import net.blay09.mods.waystones.api.IWaystone;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collection;

public class WaystonesWaypoint {
    private IClientAPI jmAPI;
    private Collection<MarkerOverlay> markers;

    public WaystonesWaypoint(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        this.markers = new ArrayList<>();
    }

    private void createMarker(IWaystone w) {
        ResourceLocation marker = new ResourceLocation("jmi:images/waystone.png");
        MapImage icon = new MapImage(marker, 32, 32)
                .setAnchorX(9.0d)
                .setAnchorY(18.0d)
                .setDisplayWidth(18.0d)
                .setDisplayHeight(18.0d)
                .setColor(14738591);

        TextProperties textProperties = new TextProperties()
                .setMinZoom(2)
                .setOpacity(1.0f);

        MarkerOverlay markerOverlay = new MarkerOverlay(JMI.MODID, "waystone_" + w.getPos(), w.getPos(), icon);
        markerOverlay.setDimension(w.getDimension()).setTitle("Waystone")
                .setLabel(w.getName())
                .setTextProperties(textProperties);

        try {
            jmAPI.show(markerOverlay);
            markers.add(markerOverlay);
        }
        catch (Exception e) {
            JMI.LOGGER.error(e);
        }
    }

    private void removeAllMarker() {
        for (MarkerOverlay o : markers) {
            jmAPI.remove(o);
        }
    }

    @SubscribeEvent
    public void onKnownWaystones(KnownWaystonesEvent event) {
        removeAllMarker();
        for (IWaystone o : event.getWaystones()) {
            createMarker(o);
        }
    }
}
