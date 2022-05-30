package frankv.jmi;

import frankv.jmi.config.ClientConfig;
import frankv.jmi.jmdefaultconfig.JMDefualtConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.network.NetworkConstants;

@Mod(JMI.MOD_ID)
public class JMIForge {
    public static final ClientConfig CLIENT_CONFIG = new ClientConfig();

    public JMIForge() {
        JMI.init(CLIENT_CONFIG, new JMIForgeEventListener());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setupClient);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT_CONFIG.getSpec());

        ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
    }

    private void setupClient(final FMLClientSetupEvent event) {
        new JMDefualtConfig(CLIENT_CONFIG).tryWriteJMDefaultConfig();
    }
}
