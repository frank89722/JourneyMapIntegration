package frankv.jmi.ftbchunks.client;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

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
        this.chunkDimPos = new ChunkDimPos(ResourceKey.create(ResourceKey.createRegistryKey(Minecraft.getInstance().level.dimension().getRegistryName()), dim), x, z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FTBClaimedChunkData chunkData = (FTBClaimedChunkData) o;
        return teamColor == chunkData.teamColor && forceLoaded == chunkData.forceLoaded && chunkDimPos.equals(chunkData.chunkDimPos) && teamName.equals(chunkData.teamName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunkDimPos, teamName, teamColor, forceLoaded);
    }
}
