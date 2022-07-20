package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.IClientConfig;
import frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import net.fabricmc.api.ClientModInitializer;

public class JMIFabric implements ClientModInitializer {
    public static final IClientConfig CLIENT_CONFIG = ClientConfig.loadConfig();

    @Override
    public void onInitializeClient() {
        JMI.init(CLIENT_CONFIG, new JMIFabricEventListener());
        new JMDefaultConfig().tryWriteJMDefaultConfig();
    }
}
