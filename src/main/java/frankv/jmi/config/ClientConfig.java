package frankv.jmi.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig {
    private ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    private final ForgeConfigSpec.BooleanValue ftbChunks;
    private final ForgeConfigSpec.BooleanValue waystone;
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> waypointMessageBlocks;
    private final ForgeConfigSpec.BooleanValue waypointMessageEmptyHandOnly;
    private final ForgeConfigSpec.DoubleValue claimedChunkOverlayOpacity;
    private final ForgeConfigSpec.BooleanValue disableFTBFunction;
    private final ForgeConfigSpec.BooleanValue showClaimChunkScreen;
    private final ForgeConfigSpec.IntValue waystoneColor;
    private final ForgeConfigSpec.IntValue defaultConfigVersion;

    public ClientConfig() {
        builder.comment("Client-Side Integration");
        builder.push("FTBChunks");
        ftbChunks = builder.comment("Enable FTBChunks Integration").define("ftbChunks", true);
        claimedChunkOverlayOpacity = builder.defineInRange("claimedChunkOverlayOpacity", 0.222223f, 0f, 1.0f);
        disableFTBFunction = builder.comment("Disable conflict functions for FTBChunks (MiniMap, Waypoint beam, Death waypoint)")
                .define("disableFTBFunction", true);
        showClaimChunkScreen = builder.comment("Show chunk claiming screen first instead of large map screen.").define("showClaimChunkScreen", true);
        builder.pop();

        builder.push("Waystones");
        waystone = builder.comment("Enable Waystones Integration").define("waystones", true);
        waystoneColor = builder.comment("The color code for Waystone marker. You can generate the color code from https://www.mathsisfun.com/hexadecimal-decimal-colors.html")
                .defineInRange("wayStoneMarkerColor", 0xffffff, 0, 16777215);
        builder.pop();

        builder.push("WaypointMessage");
        waypointMessageBlocks = builder.comment("List of block id and tags for WaypointMessage. e.g., [\"#forge:ores/diamond\", \"minecraft:diamond_block\"]").defineList("waypointMessageBlocks", ArrayList::new, l -> l instanceof String);
        waypointMessageEmptyHandOnly = builder.define("emptyHandOnly", true);
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

    public List<? extends String> getWaypointMessageBlocks() {
        return waypointMessageBlocks.get();
    }

    public Boolean getWaypointMessageEmptyHandOnly() {
        return waypointMessageEmptyHandOnly.get();
    }

    public double getClaimedChunkOverlayOpacity() {
        return claimedChunkOverlayOpacity.get();
    }

    public boolean getDisableFTBFunction() {
        return disableFTBFunction.get();
    }

    public boolean getShowClaimChunkScreen() {
        return showClaimChunkScreen.get();
    }

    public int getWaystoneColor() {
        return waystoneColor.get();
    }

    public int getDefaultConfigVersion() {
        return defaultConfigVersion.get();
    }
}
