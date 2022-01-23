package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.client.Minecraft;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public class FTBClaimedChunkData {
    public final ChunkDimPos chunkDimPos;
    public final String teamName;
    public final int teamColor;
    public final boolean forceLoaded;

    public FTBClaimedChunkData(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean forceLoaded) {
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.forceLoaded = forceLoaded;
        this.chunkDimPos = new ChunkDimPos(RegistryKey.create(RegistryKey.createRegistryKey(Minecraft.getInstance().level.dimension().getRegistryName()), dim), x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FTBClaimedChunkData chunkData = (FTBClaimedChunkData) o;
        return teamColor == chunkData.teamColor && forceLoaded == chunkData.forceLoaded && Objects.equals(chunkDimPos, chunkData.chunkDimPos) && Objects.equals(teamName, chunkData.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkDimPos, teamName, teamColor, forceLoaded);
    }

    public static class FTBChunkQueueData {
        public final FTBClaimedChunkData chunkData;
        public final boolean isAdd;
        public final boolean replace;
        public FTBChunkQueueData(FTBClaimedChunkData chunkData, boolean isAdd, boolean replace){
            this.chunkData = chunkData;
            this.isAdd = isAdd;
            this.replace = replace;
        }
    }
}
