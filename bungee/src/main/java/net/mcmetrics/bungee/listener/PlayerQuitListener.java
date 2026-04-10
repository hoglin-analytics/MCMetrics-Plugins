package net.mcmetrics.bungee.listener;

import net.mcmetrics.bungee.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerQuitListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler
    public void onQuit(final PlayerDisconnectEvent event) {
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
