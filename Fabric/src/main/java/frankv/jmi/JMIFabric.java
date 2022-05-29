package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.IClientConfig;
import net.fabricmc.api.ModInitializer;

public class JMIFabric implements ModInitializer {
    public static final IClientConfig CLIENT_CONFIG = new ClientConfig();

    public JMIFabric() {
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
//        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());
//
//        waystones = ModList.get().isLoaded("waystones");
//        ftbchunks = ModList.get().isLoaded("ftbchunks");
//
//        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void setupClient() {

//        if (ftbchunks) {
//            LOGGER.info("FTBChunk integration loaded.");
//        }
//
//        if (waystones) {
//            LOGGER.info("Waystones integration loaded.");
//        }

//        if (CLIENT_CONFIG.getDefaultConfigVersion() != -1) {
//            JMDefualtConfig.tryWriteJMDefaultConfig();
//        }
    }

    @Override
    public void onInitialize() {
        JMI.init(CLIENT_CONFIG);
    }
}
