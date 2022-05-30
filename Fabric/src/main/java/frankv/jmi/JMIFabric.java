package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.config.IClientConfig;
import net.fabricmc.api.ModInitializer;

public class JMIFabric implements ModInitializer {
    public static final IClientConfig CLIENT_CONFIG = new ClientConfig();

    @Override
    public void onInitialize() {
        JMI.init(CLIENT_CONFIG, new JMIFabricEventListener());
    }
}
