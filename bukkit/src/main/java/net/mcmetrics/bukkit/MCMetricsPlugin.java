package net.mcmetrics.bukkit;

import lombok.Getter;
import net.mcmetrics.bukkit.command.BukkitPlatformCommandManager;
import net.mcmetrics.bukkit.experiment.BukkitExperimentRunner;
import net.mcmetrics.bukkit.listener.PlayerChatListener;
import net.mcmetrics.bukkit.listener.PlayerJoinListener;
import net.mcmetrics.bukkit.listener.PlayerQuitListener;
import net.mcmetrics.bukkit.util.FoliaUtils;
import net.mcmetrics.bukkit.util.NMSUtils;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.command.PlatformCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import static net.mcmetrics.bukkit.util.FoliaUtils.IS_FOLIA;

@Getter
public class MCMetricsPlugin extends JavaPlugin {

    private static final long HEARTBEAT_INTERVAL_TICKS = 1200L;
    private static final long FOLIA_INITIAL_HEARTBEAT_DELAY_TICKS = 1L;

    private MCMetrics mcMetrics;

    @Override
    public void onEnable() {
        PlatformCommandManager commandManager = new BukkitPlatformCommandManager();

        this.mcMetrics = new MCMetrics(
                commandManager,
                getDataFolder(),
                NMSUtils::getTPS,
                NMSUtils::getMSPT,
                new BukkitExperimentRunner(),
                this::registerListeners,
                this::unregisterListeners
        );

        if (IS_FOLIA) {
            FoliaUtils.scheduleGlobal(() -> mcMetrics.newServerHeartbeatTask().run(), FOLIA_INITIAL_HEARTBEAT_DELAY_TICKS,  HEARTBEAT_INTERVAL_TICKS);
            FoliaUtils.scheduleGlobal(NMSUtils::pushMSPT, FOLIA_INITIAL_HEARTBEAT_DELAY_TICKS,  HEARTBEAT_INTERVAL_TICKS);
        } else {
            Bukkit.getScheduler().runTaskTimer(this, mcMetrics.newServerHeartbeatTask(), 0L, HEARTBEAT_INTERVAL_TICKS);
            Bukkit.getScheduler().runTaskTimer(this, NMSUtils::pushMSPT, 0L, HEARTBEAT_INTERVAL_TICKS);
        }
    }

    @Override
    public void onDisable() {
        if (IS_FOLIA) {
            FoliaUtils.cancelAllGlobalTasks();
        } else {
            Bukkit.getScheduler().cancelTasks(this);
        }
        this.mcMetrics.shutdown();
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(mcMetrics), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(mcMetrics), this);
        Bukkit.getPluginManager().registerEvents(new PlayerChatListener(mcMetrics), this);
    }

    private void unregisterListeners() {
        HandlerList.unregisterAll(this);
    }

    public static MCMetricsPlugin getInstance() {
        return JavaPlugin.getPlugin(MCMetricsPlugin.class);
    }
}
