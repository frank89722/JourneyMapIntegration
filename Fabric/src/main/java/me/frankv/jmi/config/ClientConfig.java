package me.frankv.jmi.config;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.frankv.jmi.util.FileManager;

import java.util.List;

@Data
@NoArgsConstructor
public class ClientConfig implements IClientConfig {

    private static final FileManager<ClientConfig> FILE_MANAGER = new FileManager<>("/config/jmi-client.json", ClientConfig.class);

    private Boolean ftbChunks = true;
    private Boolean waystone = true;
    private List<String> waypointMessageBlocks = Lists.newArrayList();
    private Boolean waypointMessageEmptyHandOnly = true;
    private Double claimedChunkOverlayOpacity = 0.222223;
    private Boolean disableFTBFunction = true;
    private Integer waystoneColor = 0xffffff;
    private Integer defaultConfigVersion = -1;

    public static ClientConfig loadConfig() {

        if (FILE_MANAGER.getFile().exists()) {
            return FILE_MANAGER.read();
        }

        final var config = new ClientConfig();
        FILE_MANAGER.write(config);
        return config;
    }

}
