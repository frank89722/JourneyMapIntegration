package frankv.jmi.jmdefaultconfig;

import frankv.jmi.JMI;
import frankv.jmi.util.FileManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class JMDefaultConfig {
    private Version existVersion;
    private final FileManager<Version> fileManager;

    public JMDefaultConfig() {
        fileManager = new FileManager<>("/journeymap/defaultconfig.json", Version.class);
        if (!fileManager.getFile().exists()) {
            fileManager.write(new Version(-1));
        }
        existVersion = fileManager.read();
    }

    public void tryWriteJMDefaultConfig() {
        final var version = JMI.clientConfig.getDefaultConfigVersion();

        if (version >= 0 && existVersion.version < version) {
            writeJMDefaultConfig(version);
        }
    }

    private void writeJMDefaultConfig(int newVersion) {
        final var source = new File(System.getProperty("user.dir") + "/config/jmdefaultconfig");
        final var dest = new File(System.getProperty("user.dir") + "/journeymap/");

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
