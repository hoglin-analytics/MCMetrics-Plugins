package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerJoinAnalytic;
import net.mcmetrics.common.platform.PlatformUtil;
import net.mcmetrics.common.player.TrackedPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

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

        final UUID uuid = event.getPlayer().getUniqueId();
        final TrackedPlayer player = mcMetrics.getSessionManager().addPlayer(uuid);

        player.setIp(event.getAddress().getHostAddress());
        player.setHostName(event.getHostname());
        player.setClientPlatform(PlatformUtil.getPlatform(uuid));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        final TrackedPlayer trackedPlayer = mcMetrics.getSessionManager().getPlayer(event.getPlayer().getUniqueId());
        if (trackedPlayer == null) {
            mcMetrics.getLogger().severe("TrackedPlayer not found for UUID: " + event.getPlayer().getUniqueId());
            return;
        }

        mcMetrics.getHoglin().track(new PlayerJoinAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            event.getPlayer().getUniqueId(),
            trackedPlayer,
            !event.getPlayer().hasPlayedBefore()
        ));

        mcMetrics.getConnectionManager().pushPlayerCountUpdate();
    }

}
