package net.mcmetrics.bungee.listener;

import com.fasterxml.uuid.Generators;
import net.mcmetrics.bungee.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import net.mcmetrics.common.platform.PlatformUtil;
import net.mcmetrics.common.player.TrackedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerJoinListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(final LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final UUID uuid = event.getConnection().getUniqueId();
        final TrackedPlayer player = mcMetrics.getSessionManager().addPlayer(uuid);
        final UUID sessionId = Generators.timeBasedGenerator().generate();

        player.setSessionId(sessionId.toString());
        player.setIp(event.getConnection().getAddress().getAddress().getHostAddress());
        player.setHostName(event.getConnection().getVirtualHost().getHostName());
        player.setClientPlatform(PlatformUtil.getPlatform(uuid));
        player.setSessionStart(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PostLoginEvent event) {
        final TrackedPlayer trackedPlayer = mcMetrics.getSessionManager().getPlayer(event.getPlayer().getUniqueId());
        if (trackedPlayer == null) {
            mcMetrics.getLogger().severe("TrackedPlayer not found for UUID: " + event.getPlayer().getUniqueId());
            return;
        }

        mcMetrics.getHoglin().track(new PlayerJoinAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            trackedPlayer.getSessionId(),
            event.getPlayer().getUniqueId(),
            trackedPlayer,
            false
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
