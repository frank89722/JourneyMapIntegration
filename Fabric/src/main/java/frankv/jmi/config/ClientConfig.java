package frankv.jmi.config;

import lombok.Getter;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class ClientConfig implements IClientConfig {

    @Getter
    private Boolean ftbChunks = true;
    @Getter
    private Boolean waystone = true;
    @Getter
    private List<String> waypointMessageBlocks = Lists.newArrayList();
    @Getter
    private Boolean waypointMessageEmptyHandOnly = true;
    @Getter
    private Double claimedChunkOverlayOpacity = 0.222223;
    @Getter
    private Boolean disableFTBFunction = true;
    @Getter
    private Boolean showClaimChunkScreen = true;
    @Getter
    private Integer waystoneColor = 0xffffff;
    @Getter
    private Integer defaultConfigVersion = -1;

    public ClientConfig() {

    }


}
