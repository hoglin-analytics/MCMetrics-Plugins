package net.mcmetrics.velocity;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Getter;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.mcmetrics.common.listener.PlayerChatHandler;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import net.mcmetrics.common.listener.PlayerQuitHandler;
import net.mcmetrics.velocity.command.VelocityPlatformCommandManager;
import net.mcmetrics.velocity.experiment.VelocityExperimentRunner;
import net.mcmetrics.velocity.listener.PlayerJoinListener;
import net.mcmetrics.velocity.listener.PlayerQuitListener;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

@Getter
public class MCMetricsPlugin {

    private static final long HEARTBEAT_INTERVAL_TIME = 1L;
    private static final TimeUnit HEARTBEAT_INTERVAL_UNITS = TimeUnit.MINUTES;

    private static MCMetricsPlugin INSTANCE;

    private MCMetrics mcMetrics;
    private ScheduledTask scheduledTask;

    @Inject private Injector injector;

    @Inject private ProxyServer proxyServer;

    @Inject @DataDirectory Path dataDir;

    public MCMetricsPlugin() {
        INSTANCE = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        PlatformCommandManager commandManager = new VelocityPlatformCommandManager();

        this.mcMetrics = new MCMetrics(
                commandManager,
                dataDir.toFile(),
                "config.toml",
                () -> -1.0, // Velocity doesn't have TPS
                () -> -1.0,
                new VelocityExperimentRunner(proxyServer),
                this::registerEvents,
                this::unregisterEvents,
                true
        );


        this.scheduledTask = this.proxyServer.getScheduler().buildTask(this, this.mcMetrics.newServerHeartbeatTask())
                .repeat(HEARTBEAT_INTERVAL_TIME, HEARTBEAT_INTERVAL_UNITS).schedule();
    }

    private void registerEvents(PlayerJoinHandler joinHandler, PlayerQuitHandler quitHandler, PlayerChatHandler ignored) {
        this.proxyServer.getEventManager().register(this, new PlayerJoinListener(joinHandler));
        this.proxyServer.getEventManager().register(this, new PlayerQuitListener(quitHandler));
    }

    private void unregisterEvents() {
        this.proxyServer.getEventManager().unregisterListeners(this);
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        this.scheduledTask.cancel();
        this.mcMetrics.shutdown();
    }

    public static MCMetricsPlugin getInstance() {
        return INSTANCE;
    }
}
