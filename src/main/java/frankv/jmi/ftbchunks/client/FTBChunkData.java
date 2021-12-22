package frankv.jmi.ftbchunks.client;

import net.minecraft.resources.ResourceLocation;

public class FTBChunkData {
    protected ResourceLocation dim;
    protected int x, z;
    protected String teamName;
    protected int teamColor;
    protected boolean isAdd;
    protected boolean replace;

    public FTBChunkData(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace) {
        this.dim = dim;
        this.x = x;
        this.z = z;
        this.teamName = teamName;
        this.teamColor = teamColor;
        this.isAdd = isAdd;
        this.replace = replace;
    }
}
