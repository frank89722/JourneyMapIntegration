package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.IClientConfig;
import frankv.jmi.jmdefaultconfig.JMDefaultConfig;
import net.fabricmc.api.ModInitializer;

public class JMIFabric implements ModInitializer {
    public static final IClientConfig CLIENT_CONFIG = ClientConfig.loadConfig();

    @Override
    public void onInitialize() {
        JMI.init(CLIENT_CONFIG, new JMIFabricEventListener());
        new JMDefaultConfig().tryWriteJMDefaultConfig();
    }
}
