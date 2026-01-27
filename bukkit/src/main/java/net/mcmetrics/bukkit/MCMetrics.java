package net.mcmetrics.bukkit;

import gg.hoglin.sdk.Hoglin;
import lombok.Getter;
import net.mcmetrics.bukkit.config.MCMetricsBukkitConfig;
import net.mcmetrics.bukkit.connection.ConnectionManager;
import net.mcmetrics.bukkit.listener.PlayerJoinListener;
import net.mcmetrics.bukkit.listener.PlayerQuitListener;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class MCMetrics extends JavaPlugin {

    @Getter
    private static MCMetrics instance;

    private final TomlConfigLoader<MCMetricsBukkitConfig> configLoader = new TomlConfigLoader<>(getDataFolder(), "config.toml", "default-config.toml", MCMetricsBukkitConfig.class);
    private final HoglinLoader hoglinLoader = new HoglinLoader();

    @Getter
    private MCMetricsBukkitConfig mcMetricsConfig; /** Shouldn't be called config to prevent clashes with bukkit */

    @Getter
    private ConnectionManager connectionManager = new ConnectionManager(this);

    @Override
    public void onEnable() {
        instance = this;

        mcMetricsConfig = configLoader.loadConfig();
        if (mcMetricsConfig == null) {
            getLogger().severe("Failed to find MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.");
            return;
        }

        attemptReload();
    }

    public boolean attemptReload() {
        HandlerList.unregisterAll(this);
        final boolean success = hoglinLoader.load(mcMetricsConfig.hoglin());
        if (!success) {
            getLogger().severe("Failed to load Hoglin! Ensure the Hoglin details in config.toml are correct then execute /mcmetrics reload.");
            return false;
        }

        postSuccessfulReload();

        return true;
   }

   private void postSuccessfulReload() {
       Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
       Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(this), this);
   }

    public Hoglin getHoglin() {
        return hoglinLoader.getHoglin();
    }

}
