package frankv.jmi;

import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import frankv.jmi.ftbchunks.client.ClaimedChunkPolygon;
import frankv.jmi.ftbchunks.client.ClaimingMode;
import frankv.jmi.ftbchunks.client.ClaimingModeHandler;
import frankv.jmi.ftbchunks.client.GeneralDataOverlay;
import frankv.jmi.waypointmessage.WaypointChatMessage;
import frankv.jmi.waystones.client.WaystoneMarker;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.IClientPlugin;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.FullscreenMapEvent;
import journeymap.client.api.event.RegistryEvent;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.waystones.api.KnownWaystonesEvent;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.EnumSet;

import static journeymap.client.api.event.ClientEvent.Type.*;

@ParametersAreNonnullByDefault
@journeymap.client.api.ClientPlugin
public class JMIJourneyMapPlugin implements IClientPlugin {
    private IClientAPI jmAPI = null;
    private ClaimedChunkPolygon claimedChunkPolygon;
    private ClaimingMode claimMode;
    public WaystoneMarker waystoneMarker;

    @Override
    public void initialize(final IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        JMIOverlayHelper.setJmAPI(jmAPI);

        if (JMI.ftbchunks) {
            claimedChunkPolygon = new ClaimedChunkPolygon(jmAPI);
            claimMode = new ClaimingMode(jmAPI, claimedChunkPolygon);

            MinecraftForge.EVENT_BUS.register(claimedChunkPolygon);
            MinecraftForge.EVENT_BUS.register(claimMode);
            MinecraftForge.EVENT_BUS.register(ClaimingModeHandler.class);
            MinecraftForge.EVENT_BUS.register(GeneralDataOverlay.class);
        }

        try
        {
            if (JMI.waystones)
            {
                waystoneMarker = new WaystoneMarker(jmAPI);
                Balm.getEvents().onEvent(KnownWaystonesEvent.class, event -> waystoneMarker.onKnownWaystones(event));
            }
        }
        catch (Exception e)
        {
            JMI.waystones = false;
        }
        MinecraftForge.EVENT_BUS.register(WaypointChatMessage.class);
        MinecraftForge.EVENT_BUS.register(JMIForgeEventListener.class);

        this.jmAPI.subscribe(getModId(), EnumSet.of(MAPPING_STARTED, MAPPING_STOPPED, MAP_CLICKED, MAP_DRAGGED, MAP_MOUSE_MOVED, REGISTRY));
        JMI.LOGGER.info("Initialized " + getClass().getName());
    }

    @Override
    public String getModId() {
        return JMI.MODID;
    }

    @Override
    public void onEvent(ClientEvent event) {
        try {
            switch (event.type) {
                case MAPPING_STARTED -> {
                    if (JMI.ftbchunks) {
                        if (JMIForgeEventListener.firstLogin) {
                            disableFTBChunksThings();
                        } else {
                            claimedChunkPolygon.createPolygonsOnMappingStarted();
                            JMI.LOGGER.debug("re-added ftbchunks overlays");
                        }
                    }

                    if (JMI.waystones) {
                        waystoneMarker.createMarkersOnMappingStarted();
                        JMI.LOGGER.debug("re-added waystones overlays");
                    }

                    JMIForgeEventListener.firstLogin = false;
                }

                case MAPPING_STOPPED -> {
                    clearFTBChunksOverlays();
                    WaystoneMarker.markers.clear();
                    jmAPI.removeAll(JMI.MODID);
                    JMI.LOGGER.debug("all overlays removed");
                }

                case MAP_CLICKED -> {
                    if (event instanceof FullscreenMapEvent.ClickEvent.Pre) {
                        ClaimingModeHandler.preClick((FullscreenMapEvent.ClickEvent) event);
                    }
                }

                case MAP_DRAGGED -> {
                    if (event instanceof FullscreenMapEvent.MouseDraggedEvent.Pre) {
                        ClaimingModeHandler.preDrag((FullscreenMapEvent.MouseDraggedEvent) event);
                    }
                }

                case REGISTRY -> {
                    var registryEvent = (RegistryEvent) event;

                    switch (registryEvent.getRegistryType()) {
                        case INFO_SLOT -> {
                            if (!JMI.ftbchunks) break;
                            ((RegistryEvent.InfoSlotRegistryEvent)registryEvent).register(JMI.MODID, "jmi.infoslot.ftbchunks", 1000L, ClaimedChunkPolygon::getPolygonTitleByPlayerPos);
                        }
                    }
                }

                case MAP_MOUSE_MOVED -> ClaimingModeHandler.mouseMove((FullscreenMapEvent.MouseMoveEvent) event);
            }
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void disableFTBChunksThings() {
        if (!JMI.CLIENT_CONFIG.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }

    private void clearFTBChunksOverlays() {
        ClaimedChunkPolygon.chunkOverlays.clear();
        ClaimedChunkPolygon.forceLoadedOverlays.clear();
        ClaimingModeHandler.dragPolygons.clear();
    }
}
