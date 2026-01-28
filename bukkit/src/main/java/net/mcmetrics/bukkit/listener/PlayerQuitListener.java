package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerQuitListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        mcMetrics.getHoglin().track(new PlayerQuitAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            uuid
        ));

        mcMetrics.getSessionManager().removePlayer(uuid);
        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
