package net.mcmetrics.fabric;

import lombok.Getter;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.command.PlatformCommandManager;
import net.mcmetrics.fabric.command.FabricPlatformCommandManager;
import net.mcmetrics.fabric.experiment.FabricExperimentRunner;
import net.mcmetrics.fabric.listener.PlayerChatListener;
import net.mcmetrics.fabric.listener.PlayerJoinListener;
import net.mcmetrics.fabric.listener.PlayerQuitListener;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
public class MCMetricsMod implements DedicatedServerModInitializer {

    public static final String MOD_ID = "mcmetrics";

    private static final long HEARTBEAT_INTERVAL_TIME = 1L;
    private static final TimeUnit HEARTBEAT_INTERVAL_UNITS = TimeUnit.MINUTES;

    public MCMetrics mcMetrics;
    private final HostnameStore hostnameStore = new HostnameStore();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Getter
    public static MCMetricsMod instance;

    @Override
    public void onInitializeServer() {
        instance = this;

        PlatformCommandManager commandManager = new FabricPlatformCommandManager();

        this.mcMetrics = new MCMetrics(
                commandManager,
                FabricLoader.getInstance().getConfigDir().toFile(),
                "mcmetrics-config.toml",
                TpsUtils::getTps,
                TpsUtils::getMspt,
                null,
                (j, q, c) -> {}, // Leave this empty, because I have zero clue how fabric unregisters listeners
                () -> {},
                false
        );

        // Probably a terrible way to register events
        List<Listener> listeners = List.of(
                new PlayerJoinListener(mcMetrics),
                new PlayerChatListener(mcMetrics),
                new PlayerQuitListener(mcMetrics)
        );
        listeners.forEach(Listener::register);

        executor.scheduleAtFixedRate(mcMetrics.newServerHeartbeatTask(), 0, HEARTBEAT_INTERVAL_TIME, HEARTBEAT_INTERVAL_UNITS);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.mcMetrics.setExperimentRunner(new FabricExperimentRunner(server));
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            executor.shutdown();
            mcMetrics.shutdown();
        });
    }
}
