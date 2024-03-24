package me.frankv.jmi.config;

import me.frankv.jmi.api.jmoverlay.IClientConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ClientConfig implements IClientConfig {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

    private final ModConfigSpec.BooleanValue ftbChunks;
    private final ModConfigSpec.BooleanValue waystone;
    private final ModConfigSpec.ConfigValue<List<? extends String>> waypointMessageBlocks;
    private final ModConfigSpec.BooleanValue waypointMessageEmptyHandOnly;
    private final ModConfigSpec.DoubleValue claimedChunkOverlayOpacity;
    private final ModConfigSpec.BooleanValue disableFTBFunction;
    private final ModConfigSpec.IntValue waystoneColor;
    private final ModConfigSpec.IntValue defaultConfigVersion;

    public ClientConfig() {
        builder.comment("Client-Side Integration");
        builder.push("FTBChunks");
        ftbChunks = builder.comment("Enable FTBChunks Integration").define("ftbChunks", true);
        claimedChunkOverlayOpacity = builder.defineInRange("claimedChunkOverlayOpacity", 0.222223f, 0f, 1.0f);
        disableFTBFunction = builder.comment("Disable conflict functions for FTBChunks (MiniMap, Waypoint beam, Death waypoint)")
                .define("disableFTBFunction", true);
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

    public ModConfigSpec getSpec() {
        return builder.build();
    }

    public Boolean getFtbChunks() {
        return ftbChunks.get();
    }

    public Boolean getWaystone() {
        return waystone.get();
    }

    public List<? extends String> getWaypointMessageBlocks() {
        return waypointMessageBlocks.get();
    }

    public Boolean getWaypointMessageEmptyHandOnly() {
        return waypointMessageEmptyHandOnly.get();
    }

    public Double getClaimedChunkOverlayOpacity() {
        return claimedChunkOverlayOpacity.get();
    }

    public Boolean getDisableFTBFunction() {
        return disableFTBFunction.get();
    }

    public Integer getWaystoneColor() {
        return waystoneColor.get();
    }

    public Integer getDefaultConfigVersion() {
        return defaultConfigVersion.get();
    }
}
