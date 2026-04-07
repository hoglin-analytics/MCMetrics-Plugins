package net.mcmetrics.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.AccessLevel;
import lombok.Getter;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import net.mcmetrics.common.player.SessionManager;
import net.mcmetrics.velocity.config.MCMetricsVelocityConfig;
import net.mcmetrics.velocity.connection.ConnectionManager;
import net.mcmetrics.velocity.listener.PlayerJoinListener;
import net.mcmetrics.velocity.listener.PlayerQuitListener;
import net.mcmetrics.velocity.task.ServerHeartbeatTask;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Plugin(
        id = "mcmetrics",
        name = "MCMetrics",
        version = "1.0.0"
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
    public MCMetrics(ProxyServer proxy, Logger logger, @DataDirectory Path dataDir) {
        this.configLoader = new TomlConfigLoader<>(dataDir.toFile(), "config.toml", "default-config.toml", MCMetricsVelocityConfig.class);
        this.hoglinLoader = new HoglinLoader();
        this.sessionManager = new SessionManager();
        this.connectionManager = new ConnectionManager(this);
        this.proxyServer = proxy;
        INSTANCE = this;

        this.mcMetricsConfig = this.configLoader.loadConfig();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.proxyServer.getScheduler().buildTask(this, new ServerHeartbeatTask()).repeat(1, TimeUnit.MINUTES).schedule();
        this.proxyServer.getEventManager().register(this, new PlayerJoinListener(this));
        this.proxyServer.getEventManager().register(this, new PlayerQuitListener(this));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        new ServerHeartbeatTask().run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }
    }

    public static MCMetrics getInstance() {
        return INSTANCE;
    }
}
