package frankv.jmi.jmdefaultconfig;

import frankv.jmi.JMI;

import java.io.IOException;

public class JMDefualtConfig {
    private static Version existVersion;

    public static void tryWriteJMDefaultConfig() {
        try {
            existVersion = FileManager.readConfigVersion();
        } catch (IOException e) {
            JMI.LOGGER.error("Failed to read existed default config version.");
            return;
        }

        if (existVersion.version < JMI.CLIENT_CONFIG.getDefaultConfigVersion()) {
            FileManager.writeJMDefaultConfig(JMI.CLIENT_CONFIG.getDefaultConfigVersion());
        }
    }
}
