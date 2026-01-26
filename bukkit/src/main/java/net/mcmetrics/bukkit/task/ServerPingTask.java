package net.mcmetrics.bukkit.task;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.ServerPingAnalytic;
import net.mcmetrics.common.util.BedrockUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ServerPingTask implements Runnable {

    @Override
    public void run() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();

        int javaCount = 0;
        int bedrockCount = 0;

        for (final Player player : players) {
            if (BedrockUtil.isBedrock(player.getUniqueId())) {
                bedrockCount++;
            } else {
                javaCount++;
            }
        }

        MCMetrics.getInstance().getHoglin().track(new ServerPingAnalytic(
            javaCount,
            bedrockCount
        ));
    }

}
