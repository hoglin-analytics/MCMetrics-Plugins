package net.mcmetrics.common;

import gg.hoglin.sdk.Hoglin;
import lombok.AccessLevel;
import lombok.Getter;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.mcmetrics.common.command.ReloadCommand;
import net.mcmetrics.common.command.TrackPurchaseCommand;
import net.mcmetrics.common.config.ConfigLoader;
import net.mcmetrics.common.config.MCMetricsConfig;
import net.mcmetrics.common.connection.ConnectionManager;
import net.mcmetrics.common.experiment.ExperimentManager;
import net.mcmetrics.common.experiment.ExperimentRunner;
import net.mcmetrics.common.listener.PlayerChatHandler;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import net.mcmetrics.common.listener.PlayerQuitHandler;
import net.mcmetrics.common.player.SessionManager;
import net.mcmetrics.common.task.ServerHeartbeatTask;
import net.mcmetrics.common.util.TriConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.function.Supplier;

@Getter
public class MCMetrics {

    private final PlatformCommandManager commandManager;
    private final File configDir;
    private final Supplier<Double> tpsSupplier;
    private final Supplier<Double> msptSupplier;
    private final TriConsumer<PlayerJoinHandler, PlayerQuitHandler, PlayerChatHandler> registerEvents;
    private final Runnable unregisterEvents;

    private final boolean isProxy;

    private ExperimentManager experimentManager;

    @Getter(AccessLevel.NONE)
    private final ConfigLoader<MCMetricsConfig> configLoader;

    private final HoglinLoader hoglinLoader = new HoglinLoader();
    private final SessionManager sessionManager = new SessionManager();
    private final ConnectionManager connectionManager = new ConnectionManager(this);

    private MCMetricsConfig config;

    private final Logger logger =  LoggerFactory.getLogger(MCMetrics.class);

    public MCMetrics(
            PlatformCommandManager commandManager,
            File configDir,
            Supplier<Double> tpsSupplier,
            Supplier<Double> msptSupplier,
            ExperimentRunner experimentRunner,
            TriConsumer<PlayerJoinHandler, PlayerQuitHandler, PlayerChatHandler> registerEvents,
            Runnable unregisterEvents,
            boolean isProxy
    ) {
        this.commandManager = commandManager;
        this.configDir = configDir;
        this.tpsSupplier = tpsSupplier;
        this.msptSupplier = msptSupplier;
        this.registerEvents = registerEvents;
        this.unregisterEvents = unregisterEvents;
        this.isProxy = isProxy;

        if (experimentRunner != null)
            this.experimentManager = new ExperimentManager(experimentRunner);

        this.configLoader = new ConfigLoader<>(configDir, "config.toml", "default-config.toml", MCMetricsConfig.class);

        attemptReload();
        registerBaseCommands();
    }

    public void setExperimentRunner(ExperimentRunner experimentRunner) {
        this.experimentManager = new ExperimentManager(experimentRunner);
    }

    private void registerBaseCommands() {
        commandManager.registerCommands(new ReloadCommand(this));
        commandManager.registerCommands(new TrackPurchaseCommand(this));
    }

    public Hoglin getHoglin() {
        return hoglinLoader.getHoglin();
    }

    public ServerHeartbeatTask newServerHeartbeatTask() {
        return new ServerHeartbeatTask(this);
    }

    public void shutdown() {
        newServerHeartbeatTask().run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }
    }

    public boolean attemptReload() {
        unregisterEvents.run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }

        try {
            config = configLoader.loadConfig();
        } catch (IOException e) {
            logger.error("Failed to reload MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.", e);
            return false;
        }
        if (config == null || config.hoglin() == null || config.instance() == null) {
            logger.error("Failed to reload MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.");
            return false;
        }

        final boolean success = hoglinLoader.load(config.hoglin());
        if (!success) {
            logger.error("Failed to load Hoglin! Ensure the Hoglin details in config.toml are correct then execute /mcmetrics reload.");
            return false;
        }

        registerEvents.accept(new PlayerJoinHandler(this), new PlayerQuitHandler(this), new PlayerChatHandler(this));

        return true;
    }
}
