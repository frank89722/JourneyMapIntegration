package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private ForgeConfigSpec.BooleanValue ftbChunks;
    private ForgeConfigSpec.BooleanValue waystone;
    private ForgeConfigSpec.IntValue waystoneColor;

    public ClientConfig() {
        builder.push("Client-Side Integration");
        ftbChunks = builder.define("ftbChunks", true);
        waystone = builder.define("waystones", true);
        builder.pop();
        builder.push("Waystone Marker");
        builder.comment("The color code for Waystone marker. You can generate the color code from https://www.mathsisfun.com/hexadecimal-decimal-colors.html");
        waystoneColor = builder.defineInRange("wayStoneColor", 14738591, 0, 16777215);
        builder.pop();
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

    public int getWaystoneColor() {
        return waystoneColor.get();
    }
}
