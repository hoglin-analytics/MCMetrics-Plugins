package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class PlayerJoinListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerJoinListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        mcMetrics.getHoglin().track(new PlayerJoinAnalytic(
            mcMetrics.getMcMetricsConfig().instance().reference(),
            event.getPlayer().getUniqueId(),
            event.getHostname(),
            event.getAddress().getHostAddress()
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
