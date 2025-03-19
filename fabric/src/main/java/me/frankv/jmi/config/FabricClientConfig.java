package me.frankv.jmi.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.schema.type.derived.ConfigTypes;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.PropertyMirror;
import lombok.Getter;
import me.frankv.jmi.Constants;
import me.frankv.jmi.api.jmoverlay.ClientConfig;

import java.util.Collections;
import java.util.List;

public class FabricClientConfig implements ClientConfig {

    public static final String fileName = Constants.MOD_ID + "-client.json5";

    @Getter
    private final ConfigTree configTree;

    private final PropertyMirror<Boolean> ftbChunks = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<Boolean> waystone = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<List<String>> waypointMessageBlocks = PropertyMirror.create(ConfigTypes.makeList(ConfigTypes.STRING));
    private final PropertyMirror<Boolean> waypointMessageEmptyHandOnly = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<Double> claimedChunkOverlayOpacity = PropertyMirror.create(ConfigTypes.DOUBLE);
    private final PropertyMirror<Boolean> disableFTBFunction = PropertyMirror.create(ConfigTypes.BOOLEAN);
    private final PropertyMirror<Integer> waystoneColor = PropertyMirror.create(ConfigTypes.INTEGER);
    private final PropertyMirror<Integer> defaultConfigVersion = PropertyMirror.create(ConfigTypes.INTEGER);

    public FabricClientConfig() {
        var builder = ConfigTree.builder();

        builder.fork("FTBChunks")
                .beginValue("ftbChunks", ConfigTypes.BOOLEAN, true)
                .withComment("""
                        
                        Enable FTBChunks Integration
                        Default: true
                        """)
                .finishValue(ftbChunks::mirror)

                .beginValue("claimedChunkOverlayOpacity", ConfigTypes.DOUBLE.withMinimum(0d).withMaximum(1.0d),
                        0.175)
                .withComment("""
                        
                        Range: 0 ~ 1.0, Default: 0.175
                        """)
                .finishValue(claimedChunkOverlayOpacity::mirror)

                .beginValue("disableFTBFunction", ConfigTypes.BOOLEAN, true)
                .withComment("""
                        
                        Disable conflict functions for FTBChunks (MiniMap, Waypoint beam, Death waypoint)
                        Default: true
                        """)
                .finishValue(disableFTBFunction::mirror)
                .build();

        builder.fork("Waystones")
                .beginValue("waystones", ConfigTypes.BOOLEAN, true)
                .withComment("""
                        
                        Enable Waystones Integration
                        Default: true
                        """)
                .finishValue(waystone::mirror)

                .beginValue("wayStoneMarkerColor", ConfigTypes.NATURAL.withMaximum(16777215), 0xffffff)
                .withComment("""
                        
                        The color code for Waystone marker. You can generate the color code from https://www.mathsisfun.com/hexadecimal-decimal-colors.html
                        Default: 0xffffff
                        """)
                .finishValue(waystoneColor::mirror)
                .build();

        builder.fork("WaypointMessage")
                .beginValue("waypointMessageBlocks", ConfigTypes.makeList(ConfigTypes.STRING), Collections.emptyList())
                .withComment("""
                        
                        List of block id and tags for WaypointMessage. e.g., [\\"#forge:ores/diamond\\", \\"minecraft:diamond_block\\"]
                        Default: []
                        """)
                .finishValue(waypointMessageBlocks::mirror)

                .beginValue("emptyHandOnly", ConfigTypes.BOOLEAN, true)
                .withComment("""
                        
                        Default: true
                        """)
                .finishValue(waypointMessageEmptyHandOnly::mirror)
                .build();

        builder.fork("JourneyMap Default Config")
                .beginValue("defaultConfigVersion", ConfigTypes.INTEGER.withMinimum(-1), -1)
                .withComment("""
                        
                        When local JM default config version is older than `defaultConfigVersion` it will copy everything under `/config/jmdefaultconfig/` to `/journeymap/` and replace the existing files. Set to -1 to disable.
                        Range: -1 ~ 2,147,483,647, Default: -1
                        """)
                .finishValue(defaultConfigVersion::mirror)
                .build();

        configTree = builder;
    }

    @Override
    public Boolean getFtbChunks() {
        return ftbChunks.getValue();
    }

    @Override
    public Boolean getWaystone() {
        return waystone.getValue();
    }

    @Override
    public List<? extends String> getWaypointMessageBlocks() {
        return waypointMessageBlocks.getValue();
    }

    @Override
    public Boolean getWaypointMessageEmptyHandOnly() {
        return waypointMessageEmptyHandOnly.getValue();
    }

    @Override
    public Double getClaimedChunkOverlayOpacity() {
        return claimedChunkOverlayOpacity.getValue();
    }

    @Override
    public Boolean getDisableFTBFunction() {
        return disableFTBFunction.getValue();
    }

    @Override
    public Integer getWaystoneColor() {
        return waystoneColor.getValue();
    }

    @Override
    public Integer getDefaultConfigVersion() {
        return defaultConfigVersion.getValue();
    }

}
