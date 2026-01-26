package net.mcmetrics.bukkit;

import net.mcmetrics.bukkit.config.MCMetricsBukkitConfig;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class MCMetrics extends JavaPlugin {

    private final TomlConfigLoader<MCMetricsBukkitConfig> configLoader = new TomlConfigLoader<>(getDataFolder(), "config.toml", "default-config.toml", MCMetricsBukkitConfig.class);
    private final HoglinLoader hoglinLoader = new HoglinLoader();

    private MCMetricsBukkitConfig config;

    @Override
    public void onEnable() {
        config = configLoader.loadConfig();
        if (config == null) {
            getLogger().severe("Failed to find MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.");
            return;
        }

        attemptHoglinReload();
    }

    public boolean attemptHoglinReload() {
        HandlerList.unregisterAll(this);
        final boolean success = hoglinLoader.load(config.hoglin());
        if (!success) {
            getLogger().severe("Failed to load Hoglin! Ensure the Hoglin details in config.toml are correct then execute /mcmetrics reload.");
            return false;
        }
        return true;
    }

}
