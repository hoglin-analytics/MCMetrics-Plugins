package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
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
        final TrackedPlayer trackedPlayer = this.mcMetrics.getSessionManager().getPlayer(uuid);
        if (trackedPlayer == null) {
            mcMetrics.getLogger().severe("TrackedPlayer not found for UUID: " + uuid);
            return;
        }

        long sessionTime = System.currentTimeMillis() - trackedPlayer.getSessionStart();

        mcMetrics.getHoglin().track(new PlayerQuitAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            trackedPlayer.getSessionId(),
            uuid,
            trackedPlayer.getHostName(),
            trackedPlayer.getIp(),
            trackedPlayer.getClientPlatform(),
            sessionTime
        ));

        mcMetrics.getSessionManager().removePlayer(uuid);
        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
