package frankv.jmi.jmoverlay.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.event.TeamEvent;
import frankv.jmi.JMI;
import frankv.jmi.jmoverlay.JMOverlayManager;
import frankv.jmi.jmoverlay.ToggleableOverlay;
import frankv.jmi.util.OverlayHelper;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.Context;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.ClientEvent;
import journeymap.client.api.event.RegistryEvent;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

import static frankv.jmi.util.OverlayHelper.*;

public enum ClaimedChunkPolygon implements ToggleableOverlay {
    INSTANCE;

    private IClientAPI jmAPI = null;
    private final Minecraft mc = Minecraft.getInstance();

    @Getter
    private boolean activated = true;

    @Getter
    private HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    @Getter
    private HashMap<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    @Getter
    private HashMap<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    @Getter
    private Queue<FTBClaimedChunkData> queue = new LinkedList<>();

    @Getter
    private final String buttonLabel = "FTBChunks Overlay";
    @Getter
    private final int order = 1;

    ClaimedChunkPolygon() {
        JMOverlayManager.INSTANCE.registerOverlay(this);
    }

    @Override
    public void init(IClientAPI jmAPI) {
        this.jmAPI = jmAPI;
        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
    }

    public String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        var pos = new ChunkDimPos(mc.player.level.dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
        return chunkOverlays.get(pos).getTitle();
    }

    public void onClientTick() {
        if (!isEnabled() || !JMI.clientConfig.getFtbChunks()) return;
        if (mc.level == null) return;

        for (var i = 0; i < 200; ++i) {
            if (queue == null || queue.isEmpty()) return;

            var playerDim = mc.level.dimension();
            var data = queue.poll();

            if (data.team == null) removeChunk(data, playerDim);
            else if (shouldReplace(data)) replaceChunk(data, playerDim);
            else addChunk(data, playerDim);
        }
    }

    public void createPolygonsOnMappingStarted() {
        var level = mc.level;

        if (level == null) return;

        for (var data : chunkData.values()) {
            if (!data.chunkDimPos.dimension.equals(level.dimension())) continue;
            showOverlay(data.overlay);
            chunkOverlays.put(data.chunkDimPos, data.overlay);
        }
    }

    private void addChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        var pos = data.chunkDimPos;

        if (chunkOverlays.containsKey(pos)) return;

        chunkData.put(pos, data);
        if (!pos.dimension.equals(dim)) return;

        if (!activated) {
            data.overlay.setActiveMapTypes(EnumSet.of(Context.MapType.Topo));
        }

        chunkOverlays.put(data.chunkDimPos, data.overlay);
        if (activated) showOverlay(data.overlay);

    }

    private void removeChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        var pos = data.chunkDimPos;

        if (!chunkOverlays.containsKey(pos)) return;

        chunkData.remove(pos);

        if (!pos.dimension.equals(dim)) return;

        try {
            jmAPI.remove(chunkOverlays.get(pos));
            chunkOverlays.remove(pos);
        } catch (Throwable t) {
            JMI.LOGGER.error(t.getMessage(), t);
        }
    }

    private void replaceChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        removeChunk(data, dim);
        addChunk(data, dim);
        if (ClaimingMode.INSTANCE.isActivated()) {
            showForceLoaded(data.chunkDimPos, false);
            showForceLoaded(data.chunkDimPos, true);
        }
    }

    public void showForceLoadedByArea(boolean show) {
        var level = mc.level;
        if (level == null) return;

        if (!show) {
            for (var pos : forceLoadedOverlays.keySet()) {
                chunkOverlays.get(pos).setTitle(chunkData.get(pos).team.getDisplayName());
            }

            removeOverlays(forceLoadedOverlays.values());
            forceLoadedOverlays.clear();
            return;
        }

        for (var p : ClaimingMode.INSTANCE.getArea()) {
            var chunkDimPos = new ChunkDimPos(level.dimension(), p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        }
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!chunkData.containsKey(chunkDimPos)) return;
        var data = chunkData.get(chunkDimPos);
        var teamName = data.team.getDisplayName();

        if (show && data.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            var claimedOverlay = ClaimingMode.INSTANCE.forceLoadedPolygon(chunkDimPos);
            showOverlay(claimedOverlay);
            forceLoadedOverlays.put(chunkDimPos, claimedOverlay);

            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");

        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);

            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    public void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        if (!isEnabled()) return;

        var teamId = event.getTeam().getId();
        var dim = mc.level.dimension();

        for (var data : new HashSet<>(chunkData.values())) {
            if (!data.teamId.equals(teamId)) continue;

            data.updateOverlayProps();
            replaceChunk(data, dim);
        }
    }

    private boolean shouldReplace(FTBClaimedChunkData data) {
        if (data.team == null) return false;

        var that = chunkData.get(data.chunkDimPos);
        if (that == null) return false;
        return !data.equals(that);
    }

    private void disableFTBChunksThings() {
        if (!JMI.clientConfig.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }

    private void clearOverlays() {
        chunkOverlays.clear();
        forceLoadedOverlays.clear();
    }

    @Override
    public void onToggle(IThemeButton button) {
        if (activated) {
            OverlayHelper.removeOverlays(chunkOverlays.values());
        } else {
            OverlayHelper.showOverlays(chunkOverlays.values());
        }

        activated = !activated;
        button.setToggled(activated);
    }

    @Override
    public void onJMEvent(ClientEvent event) {
        if (!isEnabled()) return;

        switch (event.type) {
            case MAPPING_STARTED -> {
                if (JMI.platformEventListener.isFirstLogin()) {
                    disableFTBChunksThings();
                } else {
                    createPolygonsOnMappingStarted();
                    JMI.LOGGER.debug("re-add ftbchunks overlays");
                }
            }

            case MAPPING_STOPPED -> clearOverlays();

            case REGISTRY -> {
                var registryEvent = (RegistryEvent) event;

                switch (registryEvent.getRegistryType()) {
                    case INFO_SLOT -> ((RegistryEvent.InfoSlotRegistryEvent) registryEvent)
                            .register(JMI.MOD_ID, "jmi.infoslot.ftbchunks", 1000L, this::getPolygonTitleByPlayerPos);
                }
            }
        }

    }

    @Override
    public boolean isEnabled() {
        return JMI.ftbchunks;
    }

    @Override
    public String getButtonIconName() {
        return "ftb";
    }

    public void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!JMI.ftbchunks) return;
        queue.offer(new FTBClaimedChunkData(dim, chunk, teamId));
    }
}