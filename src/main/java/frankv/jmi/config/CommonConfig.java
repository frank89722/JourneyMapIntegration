package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private final ForgeConfigSpec.BooleanValue ftbChunks;

    public CommonConfig() {
        builder.comment("Server-side functions that required for Client-Side integrations to work.");
        builder.push("FTBChunks");
        ftbChunks = builder.comment("Enable FTBChunks Integration").define("ftbChunks", true);
        builder.pop();
    }

    public ForgeConfigSpec getSpec() {
        return builder.build();
    }

    public boolean getFTBChunks() {
        return ftbChunks.get();
    }
}
