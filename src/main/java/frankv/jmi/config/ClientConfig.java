package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private ForgeConfigSpec.BooleanValue ftbChunks;
    private ForgeConfigSpec.BooleanValue waystone;
    private ForgeConfigSpec.BooleanValue disableFTBFunction;
    private ForgeConfigSpec.IntValue waystoneColor;
    private ForgeConfigSpec.IntValue defaultConfigVersion;

    public ClientConfig() {
        builder.comment("Client-Side Integration");
        builder.push("FTBChunks");
        ftbChunks = builder.comment("Enable FTBChunks Integration").define("ftbChunks", true);
        disableFTBFunction = builder.comment("Disable conflict functions for FTBChunks (MiniMap, Waypoint beam, Death waypoint)")
                .define("disableFTBFunction", true);
        builder.pop();

        builder.push("Waystones");
        waystone = builder.comment("Enable Waystones Integration").define("waystones", true);
        waystoneColor = builder.comment("The color code for Waystone marker. You can generate the color code from https://www.mathsisfun.com/hexadecimal-decimal-colors.html")
                .defineInRange("wayStoneMarkerColor", 0xffffff, 0, 16777215);
        builder.pop();

        builder.push("JourneyMap Default Config");
        defaultConfigVersion = builder.comment("When local JM default config version is older than `defaultConfigVersion` it will copy everything under `/config/jmdefaultconfig/` to `/journeymap/` and replace the existing files. Set to -1 to disable.")
                .defineInRange("defaultConfigVersion", -1, -1, Integer.MAX_VALUE);
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

    public boolean getDisableFTBFunction() {
        return disableFTBFunction.get();
    }

    public int getWaystoneColor() {
        return waystoneColor.get();
    }

    public int getDefaultConfigVersion() {
        return defaultConfigVersion.get();
    }
}
