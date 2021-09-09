package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.CommonConfig;
import frankv.jmi.ftbchunks.FTBChunksEventHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JMI.MODID)
public class JMI {
    public static final String MODID = "jmi";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final CommonConfig COMMON_CONFIG = new CommonConfig();
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public static boolean waystones;
    public static boolean ftbchunks;

    public JMI() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        waystones = ModList.get().isLoaded("waystones");
        ftbchunks = ModList.get().isLoaded("ftbchunks");
    }

    private void setup(final FMLCommonSetupEvent event) {
        NetworkHandler.register();

        if (ftbchunks) {
            LOGGER.info("FTBChunk integration loaded.");
            new FTBChunksEventHandler();
        }

        if (waystones) {
            LOGGER.info("Waystones integration loaded.");
        }
    }
}
