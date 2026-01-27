package net.mcmetrics.bukkit;

import gg.hoglin.sdk.Hoglin;
import lombok.Getter;
import net.mcmetrics.bukkit.command.TrackPurchaseCommand;
import net.mcmetrics.bukkit.config.MCMetricsBukkitConfig;
import net.mcmetrics.bukkit.connection.ConnectionManager;
import net.mcmetrics.bukkit.listener.PlayerChatListener;
import net.mcmetrics.bukkit.listener.PlayerJoinListener;
import net.mcmetrics.bukkit.listener.PlayerQuitListener;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class MCMetrics extends JavaPlugin {

    private final TomlConfigLoader<MCMetricsBukkitConfig> configLoader = new TomlConfigLoader<>(getDataFolder(), "config.toml", "default-config.toml", MCMetricsBukkitConfig.class);

    @Getter
    private static MCMetrics instance;

    @Getter
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

        setupCommands();
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
       Bukkit.getPluginManager().registerEvents(new PlayerChatListener(this), this);
   }

   private void setupCommands() {
       final LegacyPaperCommandManager<CommandSender> manager = new LegacyPaperCommandManager<>(
               this,
               ExecutionCoordinator.simpleCoordinator(),
               SenderMapper.identity()
       );

       if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
           manager.registerBrigadier();
       } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
           manager.registerAsynchronousCompletions();
       }

       final AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class);
       annotationParser.parse(new TrackPurchaseCommand());
   }

    public Hoglin getHoglin() {
        return hoglinLoader.getHoglin();
    }

}
