package net.mcmetrics.common.config;


import com.moandjiezana.toml.Toml;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.logging.Logger;

/**
 * A generic TOML configuration loader.
 *
 * @param <T> the type of the configuration class to load
 */
public class TomlConfigLoader<T> {

    private final Logger logger = Logger.getLogger(TomlConfigLoader.class.getName());

    /** The folder where the configuration file will be stored */
    private final File dataFolder;

    /** The configuration file that will be created/read */
    private final String configFileName;

    /** The default configuration file located in the JAR resources */
    private final String defaultResourcePath;

    /** The class to deserialize the configuration into */
    private final Class<T> configClass;

    /**
     * Creates a new TOML configuration loader
     *
     * @param dataFolder The folder where the configuration file will be stored
     * @param configFileName The name of the configuration file (e.g., "config.toml")
     * @param defaultResourcePath Path to the default config resource in the JAR (e.g., "default-config.toml")
     * @param configClass The class to deserialize the configuration into
     */
    public TomlConfigLoader(
        final File dataFolder,
        final String configFileName,
        final String defaultResourcePath,
        final Class<T> configClass
    ) {
        this.dataFolder = dataFolder;
        this.configClass = configClass;
        this.configFileName = configFileName;
        this.defaultResourcePath = defaultResourcePath;
    }

    /**
     * Loads the configuration from the specified TOML file. This method will attempt to read the configuration file if
     * it exists. If not, it will copy the default configuration from the resources and proceed to read it. If any part
     * of this process fails, it will return null and log an error.
     *
     * @return an instance of the configuration class populated with values from the TOML file, or null if loading fails
     */
    @Nullable
    public T loadConfig() {
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            logger.severe("Failed to create data folder at: " + dataFolder.getAbsolutePath());
            return null;
        }

        final File configFile = new File(dataFolder, configFileName);

        try {
            if (!configFile.exists()) {
                try (final InputStream in = getClass().getClassLoader().getResourceAsStream(defaultResourcePath)) {
                    if (in == null) {
                        logger.severe("Default config not found in resources: '/resources/" + defaultResourcePath + "'");
                        return null;
                    }
                    Files.copy(in, configFile.toPath());
                }
            }

            final Toml toml = new Toml().read(configFile);
            return toml.to(configClass);
        } catch (final IOException e) {
            logger.severe("Failed to load config file: " + configFileName + " - " + e.getMessage());
            return null;
        }
    }
}
