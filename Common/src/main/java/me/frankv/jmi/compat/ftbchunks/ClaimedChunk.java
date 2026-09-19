package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record ClaimedChunk(SendChunkPacket.SingleChunk chunk, ChunkDimPos chunkDimPos, boolean forceLoaded, UUID teamId) {

    public static ClaimedChunk create(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        long now = new Date().getTime();
        var chunkDimPos = new ChunkDimPos(dim.dimension, chunk.getX(), chunk.getZ());
        var forceLoaded = chunk.getDateInfo(true, now).forceLoaded() != null;
        return new ClaimedChunk(chunk, chunkDimPos, forceLoaded, teamId);
    }

    public Optional<ClientTeam> getTeam() {
        return ClientTeamManagerImpl.getInstance().getTeam(teamId);
    }

}
