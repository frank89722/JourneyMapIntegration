package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private ForgeConfigSpec.BooleanValue ftbChunks;

    public CommonConfig() {
        builder.comment("Toggle Server-side function that required for Client-Side integration to work.");
        ftbChunks = builder.define("ftbChunks", true);
    }

    public ForgeConfigSpec getSpec() {
        return builder.build();
    }

    public boolean getFTBChunks() {
        return ftbChunks.get();
    }
}
