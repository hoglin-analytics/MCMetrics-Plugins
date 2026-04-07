package net.mcmetrics.velocity.listener;

import com.fasterxml.uuid.Generators;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import net.mcmetrics.common.platform.PlatformUtil;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.velocity.MCMetrics;

import java.util.UUID;

public class PlayerJoinListener {

    private final MCMetrics mcMetrics;

    public PlayerJoinListener(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @Subscribe
    public void onLogin(PreLoginEvent event) {
        if (event.getResult() != PreLoginEvent.PreLoginComponentResult.allowed()) {
            return;
        }

        UUID uuid = event.getUniqueId();
        TrackedPlayer player = mcMetrics.getSessionManager().addPlayer(uuid);
        UUID sessionId = Generators.timeBasedGenerator().generate();

        player.setSessionId(sessionId.toString());
        player.setIp(event.getConnection().getRemoteAddress().getAddress().getHostAddress());
        // TODO: double check to see if this consistently returns or if we have to handle the optional properly
        player.setHostName(event.getConnection().getVirtualHost().get().getHostName());
        player.setClientPlatform(PlatformUtil.getPlatform(uuid));
        player.setSessionStart(System.currentTimeMillis());
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        TrackedPlayer trackedPlayer = mcMetrics.getSessionManager().addPlayer(event.getPlayer().getUniqueId());
        if  (trackedPlayer == null) {
            return;
        }

        mcMetrics.getHoglinLoader().getHoglin().track(new PlayerJoinAnalytic(
                mcMetrics.getMcMetricsConfig().instance().id(),
                trackedPlayer.getSessionId(),
                event.getPlayer().getUniqueId(),
                trackedPlayer,
                false  // Velocity doesn't store this
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }
}
