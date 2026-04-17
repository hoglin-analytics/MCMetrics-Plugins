package net.mcmetrics.common;

import gg.hoglin.sdk.Hoglin;
import lombok.Getter;
import net.mcmetrics.common.config.impl.HoglinConfig;
import org.jetbrains.annotations.NotNull;

public class HoglinLoader {

    @Getter
    private Hoglin hoglin;

    /**
     * Loads (or reloads) Hoglin from the specified configuration.
     *
     * @param config The Hoglin configuration to load.
     * @return true if the configuration was successfully reloaded and valid, false otherwise.
     */
    public boolean load(final @NotNull HoglinConfig config) {
        if (isLoaded()) {
            hoglin.close();
        }

        hoglin = null;

        if (!validateConfig(config)) {
            return false;
        }

        String apiServerUrl = "https://api.mcmetrics.net";
        if (config.apiServerUrl() != null && !config.apiServerUrl().isEmpty()) {
            apiServerUrl = config.apiServerUrl().replaceAll("/+$", ""); // Trim trailing slashes, our api doesn't like them for some reason
        }

        hoglin = Hoglin.builder(config.serverKey())
                .baseUrl(apiServerUrl)
                .autoFlushInterval(config.autoFlushInterval())
                .maxBatchSize(config.autoFlushMaxBatchSize())
                .build();

        hoglin.evaluateExperiment("");

        return true;
    }

    /**
     * Checks if the Hoglin instance is set and not closed
     *
     * @return true if Hoglin is loaded, false otherwise.
     */
    public boolean isLoaded() {
        return hoglin != null && !hoglin.closed();
    }

    private boolean validateConfig(final HoglinConfig config) {
        final String serverKey = config.serverKey();
        return serverKey != null && !serverKey.isEmpty();
    }

}
