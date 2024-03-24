package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.FTBChunksClientConfig;
import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.api.event.ClientTeamPropertiesChangedEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import journeymap.client.api.IClientAPI;
import journeymap.client.api.display.IThemeButton;
import journeymap.client.api.display.PolygonOverlay;
import journeymap.client.api.event.RegistryEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.jmoverlay.IClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import me.frankv.jmi.util.OverlayHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

import static me.frankv.jmi.util.OverlayHelper.removeOverlays;
import static me.frankv.jmi.util.OverlayHelper.showOverlay;

@Slf4j
public enum ClaimedChunkPolygon implements ToggleableOverlay {
    INSTANCE;

    private IClientAPI jmAPI = null;
    private final Minecraft mc = Minecraft.getInstance();
    private IClientConfig clientConfig;

    @Getter
    private boolean activated = true;

    @Getter
    private final HashMap<ChunkDimPos, PolygonOverlay> chunkOverlays = new HashMap<>();
    @Getter
    private final HashMap<ChunkDimPos, FTBClaimedChunkData> chunkData = new HashMap<>();
    @Getter
    private final HashMap<ChunkDimPos, PolygonOverlay> forceLoadedOverlays = new HashMap<>();
    private final Queue<FTBClaimedChunkData> queue = new LinkedList<>();

    @Getter
    private final String buttonLabel = "FTBChunks Overlay";
    @Getter
    private final int order = 1;


    public void init(IClientAPI jmAPI, IClientConfig clientConfig) {
        this.jmAPI = jmAPI;
        this.clientConfig = clientConfig;
//        ClaimingMode.INSTANCE = factory.get(ClaimingMode.class);

        TeamEvent.CLIENT_PROPERTIES_CHANGED.register(this::onTeamPropsChanged);
        disableFTBChunksStuff();
    }

    private String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        final var pos = new ChunkDimPos(mc.player.level().dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!chunkOverlays.containsKey(pos)) return "Wilderness";
        return chunkOverlays.get(pos).getTitle();
    }

    private void createPolygonsOnMappingStarted() {
        final var level = mc.level;

        if (level == null) return;

        chunkData.values().forEach(data -> {
            if (!data.getChunkDimPos().dimension().equals(level.dimension())) return;
            showOverlay(data.getOverlay());
            chunkOverlays.put(data.getChunkDimPos(), data.getOverlay());
        });
    }

    private void addChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        final var pos = data.getChunkDimPos();

        if (chunkOverlays.containsKey(pos)) return;

        chunkData.put(pos, data);
        if (!pos.dimension().equals(dim)) return;

        chunkOverlays.put(data.getChunkDimPos(), data.getOverlay());
        if (activated) showOverlay(data.getOverlay());

    }

    private void removeChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        final var pos = data.getChunkDimPos();
        if (!chunkOverlays.containsKey(pos)) return;
        chunkData.remove(pos);
        if (!pos.dimension().equals(dim)) return;
        try {
            jmAPI.remove(chunkOverlays.get(pos));
            chunkOverlays.remove(pos);
        } catch (Throwable t) {
            log.error(t.getMessage(), t);
        }
    }

    private void replaceChunk(FTBClaimedChunkData data, ResourceKey<Level> dim) {
        removeChunk(data, dim);
        addChunk(data, dim);
        if (ClaimingMode.INSTANCE.isActivated()) {
            showForceLoaded(data.getChunkDimPos(), false);
            showForceLoaded(data.getChunkDimPos(), true);
        }
    }

    public void showForceLoadedByArea(boolean show) {
        final var level = mc.level;
        if (level == null) return;

        if (!show) {
            forceLoadedOverlays.keySet()
                    .forEach(pos -> chunkOverlays.get(pos).setTitle(chunkData.get(pos).getTeam().getDisplayName()));

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
        final var teamName = data.getTeam().getDisplayName();

        if (show && data.isForceLoaded() && !forceLoadedOverlays.containsKey(chunkDimPos)) {
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
        if (data.getTeam() == null) return false;

        final var that = chunkData.get(data.getChunkDimPos());
        if (that == null) return false;
        return !data.equals(that);
    }

    private void disableFTBChunksStuff() {
        if (!clientConfig.getDisableFTBFunction()) return;
        FTBChunksClientConfig.DEATH_WAYPOINTS.set(false);
        FTBChunksClientConfig.MINIMAP_ENABLED.set(false);
        FTBChunksClientConfig.IN_WORLD_WAYPOINTS.set(false);
    }

    private void clearOverlays() {
        chunkOverlays.clear();
        forceLoadedOverlays.clear();
    }

    private void onTeamPropsChanged(ClientTeamPropertiesChangedEvent event) {
        final var teamId = event.getTeam().getId();
        final var dim = mc.level.dimension();

        new HashSet<>(chunkData.values()).forEach(data -> {
            if (!data.getTeamId().equals(teamId)) return;

            data.updateOverlayProps();
            replaceChunk(data, dim);
        });
    }

    public void onClientTick() {
        if (!clientConfig.getFtbChunks()) return;
        if (mc.level == null) return;

        for (var i = 0; i < 200; ++i) {
            if (queue.isEmpty()) return;

            final var playerDim = mc.level.dimension();
            final var data = queue.poll();

            if (data.getTeam() == null) removeChunk(data, playerDim);
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

    public void onJMEvent(Event.JMClientEvent e) {
        var clientEvent = e.clientEvent();

        switch (clientEvent.type) {
            case MAPPING_STARTED -> {
                if (!e.firstLogin()) {
                    createPolygonsOnMappingStarted();
                    log.debug("re-add ftbchunks overlays");
                }
            }

            case MAPPING_STOPPED -> clearOverlays();

            case REGISTRY -> {
                final var registryEvent = (RegistryEvent) clientEvent;

                if (Objects.requireNonNull(registryEvent.getRegistryType()) == RegistryEvent.RegistryType.INFO_SLOT) {
                    ((RegistryEvent.InfoSlotRegistryEvent) registryEvent)
                            .register(Constants.MOD_ID,
                                    "jmi.infoslot.ftbchunks",
                                    1000L,
                                    this::getPolygonTitleByPlayerPos);
                }
            }
        }

    }

    public void addToQueue(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        if (!clientConfig.getFtbChunks()) return;
        queue.offer(new FTBClaimedChunkData(dim, chunk, teamId));
    }

    @Override
    public String getButtonIconName() {
        return "ftb";
    }


}