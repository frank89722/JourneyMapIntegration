package frankv.jmi;

import frankv.jmi.config.IClientConfig;
import frankv.jmi.platform.Services;
import frankv.jmi.waypointmessage.WaypointChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JMI {

    public static final String MOD_ID = "jmi";
    public static final String MOD_NAME = "JourneyMap Integration";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static WaypointChatMessage waypointChatMessage;
    public static boolean waystones;
    public static boolean ftbchunks;

    public static void init(IClientConfig clientConfig) {
        waypointChatMessage = new WaypointChatMessage(clientConfig);

        waystones = Services.PLATFORM.isModLoaded("waystones");
        ftbchunks = Services.PLATFORM.isModLoaded("ftbchunks");


        if (ftbchunks) {
            LOGGER.info("FTBChunk integration loaded.");
        }

        if (waystones) {
            LOGGER.info("Waystones integration loaded.");
        }
    }

}