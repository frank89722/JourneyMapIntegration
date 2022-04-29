package frankv.jmi.jmdefaultconfig;

public class JMDefualtConfig {
    private static Version existVersion;

    public static void tryWriteJMDefaultConfig() {
        existVersion = FileManager.readConfigVersion();

//        readConfigVersionif (existVersion.version < JMI.CLIENT_CONFIG.getDefaultConfigVersion()) {
//            FileManager.writeJMDefaultConfig(JMI.CLIENT_CONFIG.getDefaultConfigVersion());
//        }
    }
}
