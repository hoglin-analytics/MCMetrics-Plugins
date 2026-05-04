package net.mcmetrics.common.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;

import java.util.UUID;

public class PlayerQuitHandler {

    private final MCMetrics mcMetrics;

    public PlayerQuitHandler(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    public void onQuit(UUID playerUUID) {
        final TrackedPlayer trackedPlayer = this.mcMetrics.getSessionManager().getPlayer(playerUUID);
        if (trackedPlayer == null) {
            mcMetrics.getLogger().error("TrackedPlayer not found for UUID: {}", playerUUID);
            return;
        }

        long sessionTime = System.currentTimeMillis() - trackedPlayer.getSessionStart();

        mcMetrics.getHoglin().track(new PlayerQuitAnalytic(
                mcMetrics.getConfig().instance().id(),
                trackedPlayer.getSessionId(),
                playerUUID,
                trackedPlayer.getHostName(),
                trackedPlayer.getIp(),
                trackedPlayer.getClientPlatform(),
                sessionTime
        ));

        mcMetrics.getSessionManager().removePlayer(playerUUID);
        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }
}
