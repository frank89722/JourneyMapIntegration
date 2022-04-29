package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.jmdefaultconfig.JMDefualtConfig;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.network.NetworkConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JMI.MODID)
public class JMI {
    public static final String MODID = "assets/jmi";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public static boolean waystones;
    public static boolean ftbchunks;

    public JMI() {
        CommonClass.init();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        waystones = ModList.get().isLoaded("waystones");
        ftbchunks = ModList.get().isLoaded("ftbchunks");

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void setupClient(final FMLClientSetupEvent event) {

        if (ftbchunks) {
            LOGGER.info("FTBChunk integration loaded.");
        }

        if (waystones) {
            LOGGER.info("Waystones integration loaded.");
        }

        if (CLIENT_CONFIG.getDefaultConfigVersion() != -1) {
            JMDefualtConfig.tryWriteJMDefaultConfig();
        }
    }
}
