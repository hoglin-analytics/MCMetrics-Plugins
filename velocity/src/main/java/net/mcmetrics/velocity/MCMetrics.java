package net.mcmetrics.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import net.mcmetrics.common.player.SessionManager;
import net.mcmetrics.velocity.command.ReloadCommand;
import net.mcmetrics.velocity.command.TrackPurchaseCommand;
import net.mcmetrics.velocity.config.MCMetricsVelocityConfig;
import net.mcmetrics.velocity.connection.ConnectionManager;
import net.mcmetrics.velocity.listener.PlayerJoinListener;
import net.mcmetrics.velocity.listener.PlayerQuitListener;
import net.mcmetrics.velocity.task.ServerHeartbeatTask;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.velocity.CloudInjectionModule;
import org.incendo.cloud.velocity.VelocityCommandManager;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(
        id = "mcmetrics",
        name = "MCMetrics",
        version = "3.0.0"
)
@Getter
public class MCMetrics {

    @Getter(AccessLevel.NONE)
    private final TomlConfigLoader<MCMetricsVelocityConfig> configLoader;

    private final HoglinLoader hoglinLoader;
    private final SessionManager sessionManager;
    private final ConnectionManager connectionManager;
    private final ProxyServer proxyServer;

    private static MCMetrics INSTANCE;

    private MCMetricsVelocityConfig mcMetricsConfig;

    @Inject
    private Injector injector;

    @Inject
    private Logger logger;

    @Inject
    public MCMetrics(ProxyServer proxy, @DataDirectory Path dataDir) {
        this.configLoader = new TomlConfigLoader<>(dataDir.toFile(), "config.toml", "default-config.toml", MCMetricsVelocityConfig.class);
        this.hoglinLoader = new HoglinLoader();
        this.sessionManager = new SessionManager();
        this.connectionManager = new ConnectionManager(this);
        this.proxyServer = proxy;
        INSTANCE = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        setupCommands();
        attemptReload();

        this.proxyServer.getScheduler().buildTask(this, new ServerHeartbeatTask()).repeat(1, TimeUnit.MINUTES).schedule();
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        new ServerHeartbeatTask().run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }
    }

    private void setupCommands() {
        final Injector childInjector = this.injector.createChildInjector(
                new CloudInjectionModule<>(
                        CommandSource.class,
                        ExecutionCoordinator.simpleCoordinator(),
                        SenderMapper.identity()
                )
        );
        final VelocityCommandManager<CommandSource> commandManager = childInjector.getInstance(
                Key.get(new TypeLiteral<>() {
                })
        );

        final AnnotationParser<CommandSource> annotationParser = new AnnotationParser<>(commandManager, CommandSource.class);
        annotationParser.parse(new TrackPurchaseCommand());
        annotationParser.parse(new ReloadCommand());
    }

    public boolean attemptReload() {
        getProxyServer().getEventManager().unregisterListeners(this);

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }

        mcMetricsConfig = configLoader.loadConfig();
        if (mcMetricsConfig == null || mcMetricsConfig.hoglin() == null || mcMetricsConfig.instance() == null) {
            logger.severe("Failed to reload MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.");
            return false;
        }

        final boolean success = hoglinLoader.load(mcMetricsConfig.hoglin());
        if (!success) {
            logger.severe("Failed to load Hoglin! Ensure the Hoglin details in config.toml are correct then execute /mcmetrics reload.");
            return false;
        }

        postSuccessfulReload();

        return true;
    }

    private void postSuccessfulReload() {
        this.proxyServer.getEventManager().register(this, new PlayerJoinListener(this));
        this.proxyServer.getEventManager().register(this, new PlayerQuitListener(this));
        this.proxyServer.getEventManager().register(this, this);
    }

    public static MCMetrics getInstance() {
        return INSTANCE;
    }
}
