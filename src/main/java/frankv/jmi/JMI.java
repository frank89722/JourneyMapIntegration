package frankv.jmi;

import frankv.jmi.ftbchunks.FTBChunksEventHandler;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(JMI.MODID)
public class JMI {
    public static final String MODID = "jmi";
    public static final Logger LOGGER = LogManager.getLogger();

    public static boolean waystones;
    public static boolean ftbchunks;

    public JMI() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

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
