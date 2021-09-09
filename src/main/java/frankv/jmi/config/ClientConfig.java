package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private ForgeConfigSpec.BooleanValue ftbChunks;
    private ForgeConfigSpec.BooleanValue waystone;

    public ClientConfig() {
        builder.comment("Toggle Client-Side integration.");
        ftbChunks = builder.define("ftbChunks", true);
        waystone = builder.define("waystones", true);

        System.out.println("Client config init");
    }

    public ForgeConfigSpec getSpec() {
        return builder.build();
    }

    public boolean getFtbChunks() {
        return ftbChunks.get();
    }

    public boolean getWayStone() {
        return waystone.get();
    }
}
