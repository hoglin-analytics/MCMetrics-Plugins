package net.mcmetrics.bukkit.connection;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.impl.ServerPlayerCountAnalytic;
import net.mcmetrics.common.util.BedrockUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ConnectionManager {

    private final MCMetrics mcMetrics;

    public ConnectionManager(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    public void pushPlayerCountUpdate() {
        int javaCount = 0;
        int bedrockCount = 0;

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (BedrockUtil.isBedrock(player.getUniqueId())) {
                bedrockCount++;
            } else {
                javaCount++;
            }
        }

        mcMetrics.getHoglin().track(new ServerPlayerCountAnalytic(
                mcMetrics.getMcMetricsConfig().instance().reference(),
                javaCount,
                bedrockCount
        ));
    }

}
