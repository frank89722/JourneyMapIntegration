package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.IClientConfig;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JMI implements ModInitializer {
    public static final String MODID = "assets/jmi";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final IClientConfig CLIENT_CONFIG = new ClientConfig();

    public static boolean waystones = true;
    public static boolean ftbchunks = true;

    public JMI() {
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
        CommonClass.init();
    }
}
