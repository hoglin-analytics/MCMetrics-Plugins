package net.mcmetrics.common.listener;

import com.fasterxml.uuid.Generators;
import gg.hoglin.sdk.models.experiment.ExperimentData;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.common.util.PlatformUtil;

import java.util.UUID;

public class PlayerJoinHandler {

    private final MCMetrics mcMetrics;

    public PlayerJoinHandler(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    public void onLogin(UUID playerUUID, String ipAddress, String hostName) {
        final TrackedPlayer player = mcMetrics.getSessionManager().addPlayer(playerUUID);
        final UUID sessionId = Generators.timeBasedGenerator().generate();

        player.setSessionId(sessionId.toString());
        player.setIp(ipAddress);
        player.setHostName(hostName);
        player.setClientPlatform(PlatformUtil.getPlatform(playerUUID));
        player.setSessionStart(System.currentTimeMillis());

        this.mcMetrics.getHoglin().addPlayerToExperimentCache(playerUUID);
    }

    public void onJoin(String playerName, UUID playerUUID, boolean isNewPlayer) {
        final TrackedPlayer trackedPlayer = this.mcMetrics.getSessionManager().getPlayer(playerUUID);
        if (trackedPlayer == null) {
            mcMetrics.getLogger().error("TrackedPlayer not found for UUID: {}", playerUUID);
            return;
        }

        mcMetrics.getHoglin().track(new PlayerJoinAnalytic(
                mcMetrics.getConfig().instance().id(),
                trackedPlayer.getSessionId(),
                playerUUID,
                trackedPlayer,
                isNewPlayer
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();

        // Fire experiments
        this.mcMetrics.getHoglin().getExperiments().values().stream()
                .filter(data -> data.getEnabled() &&
                        data.getTrigger() == ExperimentData.Trigger.JOIN)
                .forEach(data -> {
                    mcMetrics.getExperimentManager().triggerExperiment(this.mcMetrics.getHoglin(), data, playerName, playerUUID);
                });

        if (isNewPlayer) {
            this.mcMetrics.getHoglin().getExperiments().values().stream()
                    .filter(data -> data.getEnabled() &&
                            data.getTrigger() == ExperimentData.Trigger.FIRST_JOIN)
                    .forEach(data -> {
                        mcMetrics.getExperimentManager().triggerExperiment(this.mcMetrics.getHoglin(), data, playerName, playerUUID);
                    });
        }
    }
}
