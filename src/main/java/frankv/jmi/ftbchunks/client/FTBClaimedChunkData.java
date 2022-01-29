package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftbchunks.client.map.MapDimension;
import dev.ftb.mods.ftbchunks.net.SendChunkPacket;
import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import dev.ftb.mods.ftbteams.data.ClientTeam;
import dev.ftb.mods.ftbteams.data.ClientTeamManager;

import java.util.Objects;
import java.util.UUID;

public class FTBClaimedChunkData {
    public final SendChunkPacket.SingleChunk chunk;
    public final ChunkDimPos chunkDimPos;
    public final boolean forceLoaded;
    public final ClientTeam team;
    public final String teamName;
    public final int teamColor;


    public FTBClaimedChunkData(MapDimension dim, SendChunkPacket.SingleChunk chunk, UUID teamId) {
        this.chunk = chunk;
        this.chunkDimPos = new ChunkDimPos(dim.dimension, chunk.x, chunk.z);
        this.forceLoaded = chunk.forceLoaded;
        this.team = ClientTeamManager.INSTANCE.getTeam(teamId);

        if (this.team == null) {
            this.teamName = null;
            this.teamColor = 0;
        } else {
            this.teamName = this.team.getName().getString();
            this.teamColor = this.team.getColor();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FTBClaimedChunkData chunkData = (FTBClaimedChunkData) o;
        return forceLoaded == chunkData.forceLoaded && teamColor == chunkData.teamColor && Objects.equals(chunk, chunkData.chunk) && Objects.equals(chunkDimPos, chunkData.chunkDimPos) && Objects.equals(team, chunkData.team) && Objects.equals(teamName, chunkData.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, chunkDimPos, forceLoaded, team, teamName, teamColor);
    }
}
