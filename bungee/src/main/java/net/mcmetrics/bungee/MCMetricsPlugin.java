package net.mcmetrics.bungee;

import lombok.Getter;
import net.mcmetrics.bungee.command.BungeePlatformCommandManager;
import net.mcmetrics.bungee.experiment.BungeeExperimentRunner;
import net.mcmetrics.bungee.listener.PlayerJoinListener;
import net.mcmetrics.bungee.listener.PlayerQuitListener;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

@Getter
public class MCMetricsPlugin extends Plugin {

    private static final long HEARTBEAT_INTERVAL_TIME = 1L;
    private static final TimeUnit HEARTBEAT_INTERVAL_UNITS = TimeUnit.MINUTES;

    private static MCMetricsPlugin INSTANCE;

    private MCMetrics mcMetrics;

    @Override
    public void onEnable() {
        INSTANCE = this;

        PlatformCommandManager commandManager = new BungeePlatformCommandManager();

        this.mcMetrics = new MCMetrics(
                commandManager,
                getDataFolder(),
                () -> -1.0, // Bungee does not have TPS
                () -> -1.0,
                new BungeeExperimentRunner(),
                this::registerListeners,
                this::unregisterListeners
        );

        getProxy().getScheduler().schedule(this, mcMetrics.newServerHeartbeatTask(), 0, HEARTBEAT_INTERVAL_TIME, HEARTBEAT_INTERVAL_UNITS);
    }

    @Override
    public void onDisable() {
        getProxy().getScheduler().cancel(this);
        this.mcMetrics.shutdown();
    }

    private void registerListeners() {
        getProxy().getPluginManager().registerListener(this, new PlayerJoinListener(mcMetrics));
        getProxy().getPluginManager().registerListener(this, new PlayerQuitListener(mcMetrics));
    }

    private void unregisterListeners() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    public static MCMetricsPlugin getInstance() {
        return INSTANCE;
    }
}
