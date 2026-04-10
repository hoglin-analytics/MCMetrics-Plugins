package net.mcmetrics.velocity.connection;

import net.mcmetrics.common.analytic.ServerPlayerCountAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.velocity.MCMetrics;

public class ConnectionManager {

    private final MCMetrics mcMetrics;

    public ConnectionManager(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
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

        mcMetrics.getHoglinLoader().getHoglin().track(new ServerPlayerCountAnalytic(
                mcMetrics.getMcMetricsConfig().instance().id(),
                javaCount,
                bedrockCount,
                javaCount + bedrockCount
        ));
    }
}
