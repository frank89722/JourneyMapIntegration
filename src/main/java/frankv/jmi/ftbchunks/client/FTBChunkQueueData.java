package frankv.jmi.ftbchunks.client;


import net.minecraft.resources.ResourceLocation;

public class FTBChunkQueueData extends FTBClaimedChunkData {

    public final boolean isAdd;
    public final boolean replace;

    public FTBChunkQueueData(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace, boolean forceLoaded) {
        super(dim, x, z, teamName, teamColor, forceLoaded);
        this.isAdd = isAdd;
        this.replace = replace;
    }
}
