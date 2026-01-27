package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.impl.PlayerQuitAnalytic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerQuitListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        mcMetrics.getHoglin().track(new PlayerQuitAnalytic(
            mcMetrics.getMcMetricsConfig().instance().reference(),
            event.getPlayer().getUniqueId()
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
