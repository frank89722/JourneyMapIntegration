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

    private static final FileHelper<FabricClientConfig> FILE_MANAGER = new FileHelper<>("/config/jmi-client.json", FabricClientConfig.class);

    private Boolean ftbChunks = true;
    private Boolean waystone = true;
    private List<String> waypointMessageBlocks = Lists.newArrayList();
    private Boolean waypointMessageEmptyHandOnly = true;
    private Double claimedChunkOverlayOpacity = 0.222223;
    private Boolean disableFTBFunction = true;
    private Integer waystoneColor = 0xffffff;
    private Integer defaultConfigVersion = -1;

    public static FabricClientConfig loadConfig() {

        if (FILE_MANAGER.getFile().exists()) {
            return FILE_MANAGER.read();
        }

        final var config = new FabricClientConfig();
        FILE_MANAGER.write(config);
        return config;
    }

}
