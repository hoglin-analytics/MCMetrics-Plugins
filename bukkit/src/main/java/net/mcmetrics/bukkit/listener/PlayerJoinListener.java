package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.impl.PlayerJoinAnalytic;
import net.mcmetrics.common.analytic.impl.ServerPlayerCountAnalytic;
import net.mcmetrics.common.util.BedrockUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        MCMetrics.getInstance().getHoglin().track(new PlayerJoinAnalytic(
            MCMetrics.getInstance().getConfig().instance().reference(),
            event.getPlayer().getUniqueId(),
            event.getHostname(),
            event.getAddress().getHostAddress()
        ));

        pushPlayerCountUpdate();
    }

    private void pushPlayerCountUpdate() {
        int javaCount = 0;
        int bedrockCount = 0;

        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (BedrockUtil.isBedrock(player.getUniqueId())) {
                bedrockCount++;
            } else {
                javaCount++;
            }
        }

        MCMetrics.getInstance().getHoglin().track(new ServerPlayerCountAnalytic(
            MCMetrics.getInstance().getConfig().instance().reference(),
            javaCount,
            bedrockCount
        ));
    }

}
