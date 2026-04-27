package net.mcmetrics.bukkit.connection;

import lombok.SneakyThrows;
import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.server.ServerPerformanceAnalytic;
import net.mcmetrics.common.analytic.server.ServerPlayerCountAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import org.bukkit.Bukkit;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConnectionManager {

    private final MCMetrics mcMetrics;
    private final OperatingSystemMXBean osBean;
    private final Runtime runtime;

    public ConnectionManager(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
        this.osBean = ManagementFactory.getOperatingSystemMXBean();
        this.runtime = Runtime.getRuntime();
    }

    public void pushPlayerCountUpdate() {
        int javaCount = 0;
        int bedrockCount = 0;

        for (final TrackedPlayer player : mcMetrics.getSessionManager().getAllPlayers()) {
            switch (player.getClientPlatform()) {
                case JAVA -> javaCount++;
                case BEDROCK -> bedrockCount++;
            }
        }

        mcMetrics.getHoglin().track(new ServerPlayerCountAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            javaCount,
            bedrockCount,
            javaCount + bedrockCount
        ));
    }

    @SneakyThrows
    public void pushPerformanceUpdate() {
        double tps = Bukkit.getTPS()[0]; // In Folia this would get the average TPS across all loaded regions
        double mspt = Bukkit.getAverageTickTime();
        double cpuUsage = osBean.getSystemLoadAverage();
        double memUsage = runtime.totalMemory() - runtime.freeMemory();

        FileStore fileStore = Files.getFileStore(Paths.get("/"));
        double diskUsage = fileStore.getTotalSpace() - fileStore.getUnallocatedSpace();

        mcMetrics.getHoglin().track(new ServerPerformanceAnalytic(
                cpuUsage,
                memUsage,
                diskUsage,
                tps,
                mspt
        ));
    }

}
