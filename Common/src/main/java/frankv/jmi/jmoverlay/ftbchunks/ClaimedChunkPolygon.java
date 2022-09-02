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
        disableFTBChunksStuff();
    }

    private String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        final var pos = new ChunkDimPos(mc.player.level.dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
        return chunkOverlays.get(pos).getTitle();
    }

    private void createPolygonsOnMappingStarted() {
        final var level = mc.level;

        if (level == null) return;

        chunkData.values().forEach(data -> {
            if (!data.chunkDimPos.dimension.equals(level.dimension())) return;
            showOverlay(data.overlay);
            chunkOverlays.put(data.chunkDimPos, data.overlay);
        });
    }

    private void addChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        final var pos = data.chunkDimPos;

        if (chunkOverlays.containsKey(pos)) return;

        chunkData.put(pos, data);
        if (!pos.dimension.equals(dim)) return;

        chunkOverlays.put(data.chunkDimPos, data.overlay);
        if (activated) showOverlay(data.overlay);

    }

    private void removeChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        final var pos = data.chunkDimPos;

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
        final var level = mc.level;
        if (level == null) return;

        if (!show) {
            forceLoadedOverlays.keySet()
                    .forEach(pos -> chunkOverlays.get(pos).setTitle(chunkData.get(pos).team.getDisplayName()));

            removeOverlays(forceLoadedOverlays.values());
            forceLoadedOverlays.clear();
            return;
        }

        ClaimingMode.INSTANCE.getArea().forEach(p -> {
            final var chunkDimPos = new ChunkDimPos(level.dimension(), p.x, p.z);
            showForceLoaded(chunkDimPos, true);
        });
    }

    private void showForceLoaded(ChunkDimPos chunkDimPos, boolean show) {
        if (!chunkData.containsKey(chunkDimPos)) return;
        final var data = chunkData.get(chunkDimPos);
        final var teamName = data.team.getDisplayName();

        if (show && data.forceLoaded && !forceLoadedOverlays.containsKey(chunkDimPos)) {
            final var claimedOverlay = ClaimingMode.INSTANCE.forceLoadedPolygon(chunkDimPos);
            showOverlay(claimedOverlay);
            forceLoadedOverlays.put(chunkDimPos, claimedOverlay);
            chunkOverlays.get(chunkDimPos).setTitle(teamName + "\nForce Loaded");
        } else if (!show && forceLoadedOverlays.containsKey(chunkDimPos)) {
            jmAPI.remove(forceLoadedOverlays.get(chunkDimPos));
            forceLoadedOverlays.remove(chunkDimPos);
            chunkOverlays.get(chunkDimPos).setTitle(teamName);
        }
    }

    private boolean shouldReplace(FTBClaimedChunkData data) {
        if (data.team == null) return false;

        final var that = chunkData.get(data.chunkDimPos);
        if (that == null) return false;
        return !data.equals(that);
    }

    private void disableFTBChunksStuff() {
        if (!JMI.clientConfig.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }

    private void clearOverlays() {
        chunkOverlays.clear();
        forceLoadedOverlays.clear();
    }

    private void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        if (!isEnabled()) return;

        final var teamId = event.getTeam().getId();
        final var dim = mc.level.dimension();

        new HashSet<>(chunkData.values()).forEach(data -> {
            if (!data.teamId.equals(teamId)) return;

            data.updateOverlayProps();
            replaceChunk(data, dim);
        });
    }

    public void onClientTick() {
        if (!isEnabled() || !JMI.clientConfig.getFtbChunks()) return;
        if (mc.level == null) return;

        for (var i = 0; i < 200; ++i) {
            if (queue == null || queue.isEmpty()) return;

            final var playerDim = mc.level.dimension();
            final var data = queue.poll();

            if (data.team == null) removeChunk(data, playerDim);
            else if (shouldReplace(data)) replaceChunk(data, playerDim);
            else addChunk(data, playerDim);
        }
    }

    private Boolean shouldToggleAfterOff = false;
    void onClaiming(boolean off) {
        if (!off && activated) return;
        if (!off) {
            toggleOverlay();
            shouldToggleAfterOff = true;
        } else if (shouldToggleAfterOff) {
            toggleOverlay();
            shouldToggleAfterOff = false;
        }
    }

    private void toggleOverlay() {
        if (activated) {
            OverlayHelper.removeOverlays(chunkOverlays.values());
        } else {
            OverlayHelper.showOverlays(chunkOverlays.values());
        }

        activated = !activated;
    }
    @Override
    public void onToggle(IThemeButton button) {
        if (ClaimingMode.INSTANCE.isActivated()) return;
        toggleOverlay();
        button.setToggled(activated);
    }

    @Override
    public void onJMEvent(ClientEvent event) {
        if (!isEnabled()) return;

        switch (event.type) {
            case MAPPING_STARTED -> {
                if (!JMI.platformEventListener.isFirstLogin()) {
                    createPolygonsOnMappingStarted();
                    JMI.LOGGER.debug("re-add ftbchunks overlays");
                }
            }

            case MAPPING_STOPPED -> clearOverlays();

            case REGISTRY -> {
                final var registryEvent = (RegistryEvent) event;

                switch (registryEvent.getRegistryType()) {
                    case INFO_SLOT -> ((RegistryEvent.InfoSlotRegistryEvent) registryEvent)
                            .register(JMI.MOD_ID, "jmi.infoslot.ftbchunks", 1000L, this::getPolygonTitleByPlayerPos);
                }
            }
        }

    }

    public void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!JMI.ftbchunks) return;
        queue.offer(new FTBClaimedChunkData(dim, chunk, teamId));
    }

    @Override
    public boolean isEnabled() {
        return JMI.ftbchunks;
    }

    @Override
    public String getButtonIconName() {
        return "ftb";
    }


}