package me.frankv.jmi.config;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.frankv.jmi.api.jmoverlay.ClientConfig;
import me.frankv.jmi.util.FileHelper;

import java.util.List;

@Data
@NoArgsConstructor
public class FabricClientConfig implements ClientConfig {

    private static final FileHelper<FabricClientConfig> FILE_HELPER = new FileHelper<>("/config/jmi-client.json", FabricClientConfig.class);

    private Boolean ftbChunks = true;
    private Boolean waystone = true;
    private List<String> waypointMessageBlocks = Lists.newArrayList();
    private Boolean waypointMessageEmptyHandOnly = true;
    private Double claimedChunkOverlayOpacity = 0.175;
    private Boolean disableFTBFunction = true;
    private Integer waystoneColor = 0xffffff;
    private Integer defaultConfigVersion = -1;

    public static FabricClientConfig loadConfig() {

        if (FILE_HELPER.getFile().exists()) {
            final var loaded = FILE_HELPER.read();
            if (loaded != null) return loaded;
        }

        final var config = new FabricClientConfig();
        FILE_HELPER.write(config);
        return config;
    }

}
