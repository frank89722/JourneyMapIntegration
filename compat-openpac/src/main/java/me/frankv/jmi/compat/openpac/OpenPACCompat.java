package me.frankv.jmi.compat.openpac;

import journeymap.api.v2.client.IClientAPI;
import me.frankv.jmi.api.ModCompat;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.api.event.JMIEventBus;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.api.jmoverlay.ToggleableOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ChunkPos;
import xaero.pac.client.IClientDataAPI;
import xaero.pac.client.api.OpenPACClientAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

public class OpenPACCompat implements ModCompat {
    private final Map<ChunkPos, ?> state = new HashMap<>();
    private
    private OpenPACClientAPI api;
    private final Minecraft mc = Minecraft.getInstance();
    private IClientAPI jmAPI;

    @Override
    public void init(IClientAPI jmAPI, ClientConfig clientConfig) {
        this.jmAPI = jmAPI;
        api = OpenPACClientAPI.get();
        api.getClaimsManager().getTracker().register(OpenPACClaimsListener.INSTANCE);
        api.getClaimsManager().getDimension(Minecraft.getInstance().level.dimension().location()).getCount()
    }

    @Override
    public void registerEvent(JMIEventBus eventBus) {
        eventBus.subscribe(Event.ClientTick.class, __ -> clientTick());
    }

    @Override
    public Set<ToggleableOverlay> getToggleableOverlays() {
        return Set.of();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean isTargetModsLoaded() {
        return true;
    }

    private void clientTick() {
        if (api.getClaimsManager().isLoading()) return;


    }

    private void loadRegion(int regionX, int regionZ) {
        var level = Optional.ofNullable(mc.player)
                .map(o -> o.clientLevel)
                .filter(o -> o.isClientSide)
                .orElseThrow(IllegalStateException::new);

        IntStream.of(0, 32)
                .boxed()
                .flatMap(x -> IntStream.range(0, 32).mapToObj(z -> new int[]{x, z}))
                .forEach(coord -> loadChunk(level, new ChunkPos(coord[0], coord[1])));

    }

    private void loadChunk(ClientLevel clientLevel, ChunkPos chunkPos) {
        var dim = clientLevel.dimension().location();

        Optional.ofNullable(api.getClaimsManager().get(dim, chunkPos))
                .ifPresent();

    }
}
