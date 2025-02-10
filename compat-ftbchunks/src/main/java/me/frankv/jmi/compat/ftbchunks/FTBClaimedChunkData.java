package me.frankv.jmi.compat.ftbchunks;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.data.ChunkSyncInfo;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManagerImpl;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public record FTBClaimedChunkData(ChunkSyncInfo chunk, ChunkDimPos chunkDimPos, boolean forceLoaded, UUID teamId) {

    public static FTBClaimedChunkData create(MapDimension dim, ChunkSyncInfo chunk, UUID teamId) {
        long now = new Date().getTime();
        var chunkDimPos = new ChunkDimPos(dim.dimension, chunk.x(), chunk.z());
        var forceLoaded = chunk.getDateInfo(true, now).forceLoaded() != null;
        return new FTBClaimedChunkData(chunk, chunkDimPos, forceLoaded, teamId);
    }

    public Optional<ClientTeam> getTeam() {
        return ClientTeamManagerImpl.getInstance().getTeam(teamId);
    }

}
