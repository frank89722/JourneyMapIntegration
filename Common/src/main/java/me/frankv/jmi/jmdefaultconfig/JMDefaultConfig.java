package me.frankv.jmi.jmdefaultconfig;

import lombok.extern.slf4j.Slf4j;
import me.frankv.jmi.JMI;
import me.frankv.jmi.util.FileHelper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

@Slf4j
public class JMDefaultConfig {
    private final Version existVersion;
    private final FileHelper<Version> fileHelper;

    public JMDefaultConfig() {
        fileHelper = new FileHelper<>("/journeymap/defaultconfig.json", Version.class);
        if (!fileHelper.getFile().exists()) {
            fileHelper.write(new Version(-1));
        }
        existVersion = fileHelper.read();
    }

    public void tryWriteJMDefaultConfig() {
        final var version = JMI.getClientConfig().getDefaultConfigVersion();

        if (version >= 0 && existVersion.version() < version) {
            writeJMDefaultConfig(version);
        }
    }

    private void writeJMDefaultConfig(int newVersion) {
        final var source = new File(System.getProperty("user.dir") + "/config/jmdefaultconfig");
        final var dest = new File(System.getProperty("user.dir") + "/journeymap/");

        if (!source.exists() || !source.isDirectory()) {
            log.warn("No default config found.");
            return;
        }

        log.info("Writing default configs for Journeymap...");
        try {
            fileHelper.write(new Version(newVersion));
            FileUtils.copyDirectory(source, dest);
            log.info("Journeymap configs updated.");
        } catch (IOException e) {
            log.error("Failed to write default configs " + e);
        }
    }

    private record Version(int version) {}
}
