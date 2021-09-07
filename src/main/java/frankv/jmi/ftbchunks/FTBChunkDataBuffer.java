package frankv.jmi.ftbchunks;

import net.minecraft.util.ResourceLocation;

public class FTBChunkDataBuffer {
    protected ResourceLocation dim;
    protected int x, z;
    protected String teamName;
    protected int teamColor;
    protected boolean isAdd;
    protected boolean replace;

    public FTBChunkDataBuffer(ResourceLocation dim, int x, int z, String teamName, int teamColor, boolean isAdd, boolean replace) {
        this.dim = dim;
        this.x = x;
        this.z = z;
        this.teamName = teamName;
        System.out.println(this.teamName);
        this.teamColor = teamColor;
        this.isAdd = isAdd;
        this.replace = replace;
    }
}
