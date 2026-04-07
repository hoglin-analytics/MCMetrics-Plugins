package net.mcmetrics.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.velocity.MCMetrics;

import java.util.UUID;

public class PlayerQuitListener {

    private final MCMetrics mcMetrics;

    public PlayerQuitListener(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        TrackedPlayer trackedPlayer = this.mcMetrics.getSessionManager().getPlayer(uuid);
        if (trackedPlayer == null) {
            return;
        }

        long sessionTime = System.currentTimeMillis() - trackedPlayer.getSessionStart();

        mcMetrics.getHoglinLoader().getHoglin().track(new PlayerQuitAnalytic(
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
