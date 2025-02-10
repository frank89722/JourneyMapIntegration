package me.frankv.jmi.compat.ftbchunks.infoslot;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import lombok.AllArgsConstructor;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.event.Event;
import me.frankv.jmi.compat.ftbchunks.ClaimedChunksOverlayStates;
import net.minecraft.client.Minecraft;

@AllArgsConstructor
public class ClaimedChunkInfoSlot {
    private final Minecraft mc = Minecraft.getInstance();
    private final ClaimedChunksOverlayStates states;

    public void onJMInfoSlotRegistryEvent(Event.JMInfoSlotRegistryEvent e) {
        e.infoSlotRegistryEvent().register(Constants.MOD_ID,
                "jmi.infoslot.ftbchunks",
                1000L,
                this::getPolygonTitleByPlayerPos);
    }

    private String getPolygonTitleByPlayerPos() {
        if (mc.player == null) return "";

        final var pos = new ChunkDimPos(mc.player.level().dimension(), mc.player.chunkPosition().x, mc.player.chunkPosition().z);
        if (!states.getChunkData().containsKey(pos)) return "Wilderness";
        return states.getChunkData().get(pos).getTeam().map(ClientTeam::getDisplayName).orElse("");
    }

}
