package frankv.jmi.jmdefaultconfig;

import frankv.jmi.JMI;

public class JMDefualtConfig {


    private static FileManager.Version existVersion;

    public static void tryWriteJMDefaultConfig() {
        existVersion = FileManager.readConfigVersion();

        if (existVersion.version() < JMI.CLIENT_CONFIG.getDefaultConfigVersion()) {
            FileManager.writeJMDefaultConfig(JMI.CLIENT_CONFIG.getDefaultConfigVersion());
        }
    }
}
