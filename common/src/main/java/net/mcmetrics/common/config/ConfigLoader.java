package net.mcmetrics.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema;
import com.fasterxml.jackson.dataformat.toml.TomlMapper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;
import java.util.logging.Logger;

public class ConfigLoader<T> {

    private static final String PROPS_PREFIX = "mcmetrics";

    private final File dataFolder;
    private final Class<T> configClass;
    private final String configFileName;
    private final String defaultResourcePath;

    private final Logger logger = Logger.getLogger(this.getClass().getName());

    public ConfigLoader(
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

    public T loadConfig() throws IOException {
        T config = loadToml();

        JavaPropsMapper propsMapper = new JavaPropsMapper();
        propsMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        propsMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Hacky method to take the set JVM properties and overwrite the TOML properties
        JsonParser parser = propsMapper.getFactory().createParser(getJVMProperties());
        parser.setSchema(JavaPropsSchema.emptySchema());
        config = propsMapper.readerForUpdating(config).readValue(parser);

        return config;
    }

    private Properties getJVMProperties() {
        Properties props = new Properties();

        for (String key : System.getProperties().stringPropertyNames()) {
            if (!key.startsWith(PROPS_PREFIX + ".")) {
                continue;
            }

            String strippedKey = key.substring(PROPS_PREFIX.length() + 1);
            props.setProperty(strippedKey, System.getProperty(key));
        }

        return props;
    }

    private T loadToml() {
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

            return new TomlMapper()
                    .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(configFile, configClass);
        } catch (final IOException e) {
            logger.severe("Failed to load config file: " + configFileName + " - " + e.getMessage());
            return null;
        }
    }
}
