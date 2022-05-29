package frankv.jmi.jmdefaultconfig;

import frankv.jmi.JMI;
import frankv.jmi.config.IClientConfig;
import frankv.jmi.util.FileManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JMDefualtConfig {
    private Version existVersion;
    private FileManager<Version> fileManager;
    private IClientConfig clientConfig;

    public JMDefualtConfig(IClientConfig config) {
        fileManager = new FileManager<>("/journeymap/defaultconfig.json");
        if (!fileManager.getFile().exists()) {
            fileManager.write(new Version(-1));
        }
        existVersion = fileManager.read(Version.class);
        clientConfig = config;
    }

    public void tryWriteJMDefaultConfig() {
        if (existVersion.version < clientConfig.getDefaultConfigVersion()) {
            writeJMDefaultConfig(clientConfig.getDefaultConfigVersion());
        }
    }

    private void writeJMDefaultConfig(int newVersion) {
        var source = new File(System.getProperty("user.dir") + "/config/jmdefaultconfig");
        var dest = new File(System.getProperty("user.dir") + "/journeymap/");

        if (!source.exists() || !source.isDirectory()) {
            JMI.LOGGER.warn("No default config found.");
            return;
        }

        JMI.LOGGER.info("Writing default configs for Journeymap...");
        try {
            fileManager.write(new Version(newVersion));
            FileUtils.copyDirectory(source, dest);
            JMI.LOGGER.info("Journeymap configs updated.");
        } catch (IOException e) {
            JMI.LOGGER.error("Failed to write default configs " + e);
        }
    }
}
