package net.mcmetrics.bukkit;

import gg.hoglin.sdk.Hoglin;
import lombok.AccessLevel;
import lombok.Getter;
import net.mcmetrics.bukkit.command.ReloadCommand;
import net.mcmetrics.bukkit.command.TrackPurchaseCommand;
import net.mcmetrics.bukkit.config.MCMetricsBukkitConfig;
import net.mcmetrics.bukkit.connection.ConnectionManager;
import net.mcmetrics.bukkit.listener.PlayerChatListener;
import net.mcmetrics.bukkit.listener.PlayerJoinListener;
import net.mcmetrics.bukkit.listener.PlayerQuitListener;
import net.mcmetrics.bukkit.runnable.ServerHeartbeatTask;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import net.mcmetrics.common.player.SessionManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

@Getter
public class MCMetrics extends JavaPlugin {

    @Getter(AccessLevel.NONE)
    private final TomlConfigLoader<MCMetricsBukkitConfig> configLoader = new TomlConfigLoader<>(getDataFolder(), "config.toml", "default-config.toml", MCMetricsBukkitConfig.class);

    private final HoglinLoader hoglinLoader = new HoglinLoader();
    private final SessionManager sessionManager = new SessionManager();
    private final ConnectionManager connectionManager = new ConnectionManager(this);

    private MCMetricsBukkitConfig mcMetricsConfig; /** Shouldn't be called 'config' to prevent clashes with bukkit */

    @Override
    public void onEnable() {
        setupCommands();
        attemptReload();

        Bukkit.getScheduler().runTaskTimer(this, new ServerHeartbeatTask(), 0, 1200); // 1 minute interval
    }

    @Override
    public void onDisable() {
        new ServerHeartbeatTask().run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }
    }

    public boolean attemptReload() {
        HandlerList.unregisterAll(this);

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }

        mcMetricsConfig = configLoader.loadConfig();
        if (mcMetricsConfig == null || mcMetricsConfig.hoglin() == null || mcMetricsConfig.instance() == null) {
            getLogger().severe("Failed to reload MCMetrics config! Ensure your config.toml configuration is correct then execute /mcmetrics reload.");
            return false;
        }

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
       annotationParser.parse(new ReloadCommand());
   }

    public Hoglin getHoglin() {
        return hoglinLoader.getHoglin();
    }

    public static MCMetrics getInstance() {
        return JavaPlugin.getPlugin(MCMetrics.class);
    }

}
