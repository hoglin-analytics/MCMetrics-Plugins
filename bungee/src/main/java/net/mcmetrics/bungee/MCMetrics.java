package net.mcmetrics.bungee;

import gg.hoglin.sdk.Hoglin;
import lombok.AccessLevel;
import lombok.Getter;
import net.mcmetrics.bungee.command.ReloadCommand;
import net.mcmetrics.bungee.command.TrackPurchaseCommand;
import net.mcmetrics.bungee.config.MCMetricsBungeeConfig;
import net.mcmetrics.bungee.connection.ConnectionManager;
import net.mcmetrics.bungee.listener.PlayerJoinListener;
import net.mcmetrics.bungee.listener.PlayerQuitListener;
import net.mcmetrics.bungee.task.ServerHeartbeatTask;
import net.mcmetrics.common.HoglinLoader;
import net.mcmetrics.common.config.TomlConfigLoader;
import net.mcmetrics.common.player.SessionManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Plugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bungee.BungeeCommandManager;
import org.incendo.cloud.execution.ExecutionCoordinator;

import java.util.concurrent.TimeUnit;

@Getter
public class MCMetrics extends Plugin {

    @Getter(AccessLevel.NONE)
    private final TomlConfigLoader<MCMetricsBungeeConfig> configLoader = new TomlConfigLoader<>(getDataFolder(), "config.toml", "default-config.toml", MCMetricsBungeeConfig.class);

    private final HoglinLoader hoglinLoader = new HoglinLoader();
    private final SessionManager sessionManager = new SessionManager();
    private final ConnectionManager connectionManager = new ConnectionManager(this);

    private MCMetricsBungeeConfig mcMetricsConfig; /** Shouldn't be called 'config' to prevent clashes with bukkit */

    @Getter
    private static MCMetrics instance;

    @Override
    public void onEnable() {
        instance = this;

        setupCommands();
        attemptReload();

        getProxy().getScheduler().schedule(this, new ServerHeartbeatTask(), 0, 1, TimeUnit.MINUTES); // 1 minute interval
    }

    @Override
    public void onDisable() {
        new ServerHeartbeatTask().run();

        if (hoglinLoader.isLoaded()) {
            hoglinLoader.getHoglin().close();
        }
    }

    public boolean attemptReload() {
        getProxy().getPluginManager().unregisterListeners(this);

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
       getProxy().getPluginManager().registerListener(this, new PlayerJoinListener(this));
       getProxy().getPluginManager().registerListener(this, new PlayerQuitListener(this));
   }

   private void setupCommands() {
       final BungeeCommandManager<CommandSender> manager = new BungeeCommandManager<>(
               this,
               ExecutionCoordinator.simpleCoordinator(),
               SenderMapper.identity()
       );

       final AnnotationParser<CommandSender> annotationParser = new AnnotationParser<>(manager, CommandSender.class);
       annotationParser.parse(new TrackPurchaseCommand());
       annotationParser.parse(new ReloadCommand());
   }

    public Hoglin getHoglin() {
        return hoglinLoader.getHoglin();
    }
}
