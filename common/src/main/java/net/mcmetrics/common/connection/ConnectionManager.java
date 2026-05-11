package net.mcmetrics.common.connection;

import lombok.SneakyThrows;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.server.ServerPerformanceAnalytic;
import net.mcmetrics.common.analytic.server.ServerPlayerCountAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.common.util.AtomicDouble;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConnectionManager {

    private final MCMetrics mcMetrics;

    private final ThreadMXBean threadMXBean;
    private final FileStore fileStore;
    private final Runtime runtime;

    private final long allocatedMemory;
    private final long diskSize;

    private final AtomicDouble tps;
    private final AtomicDouble mspt;


    @SneakyThrows
    public ConnectionManager(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;

        this.threadMXBean = ManagementFactory.getThreadMXBean();
        this.fileStore = Files.getFileStore(Paths.get("").toAbsolutePath());
        this.diskSize = fileStore.getTotalSpace();

        this.runtime = Runtime.getRuntime();
        this.allocatedMemory = this.runtime.maxMemory();

        this.tps = new AtomicDouble();
        this.mspt = new AtomicDouble();
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
            mcMetrics.getConfig().instance().id(),
            javaCount,
            bedrockCount,
            javaCount + bedrockCount
        ));
    }

    @SneakyThrows
    public void pushPerformanceUpdate() {
        double tps = this.tps.get();
        double mspt = this.mspt.get();

        long cpuTime = this.threadMXBean.getCurrentThreadCpuTime();
        long memUsage = this.allocatedMemory - runtime.freeMemory();
        long diskUsage = this.diskSize - this.fileStore.getUsableSpace();

        mcMetrics.getHoglin().track(new ServerPerformanceAnalytic(
                mcMetrics.getConfig().instance().id(),
                cpuTime,
                memUsage,
                this.allocatedMemory,
                diskUsage,
                this.diskSize,
                tps,
                mspt
        ));
    }

    public void updateTPS(final double tps, final double mspt) {
        this.tps.getAndSet(tps);
        this.mspt.getAndSet(mspt);
    }
}
