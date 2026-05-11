package net.mcmetrics.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import net.mcmetrics.velocity.MCMetricsPlugin;

import java.util.UUID;

public class PlayerJoinListener {

    private final PlayerJoinHandler playerJoinHandler;

    public PlayerJoinListener(final PlayerJoinHandler playerJoinHandler) {
        this.playerJoinHandler = playerJoinHandler;
    }

    @Subscribe
    public void onLogin(PreLoginEvent event) {
        if (event.getResult() != PreLoginEvent.PreLoginComponentResult.allowed()) {
            return;
        }

        UUID playerUUID = event.getUniqueId();
        String ipAddress = event.getConnection().getRemoteAddress().getAddress().getHostAddress();
        String hostName = event.getConnection().getVirtualHost().orElse(MCMetricsPlugin.getInstance().getProxyServer().getBoundAddress()).getHostString();

        this.playerJoinHandler.onLogin(playerUUID, ipAddress, hostName);
    }

    @Subscribe
    public void onJoin(PostLoginEvent event) {
        String playerName = event.getPlayer().getUsername();
        UUID playerUUID = event.getPlayer().getUniqueId();

        // Velocity doesn't track new players, maybe we write our own solution to this later?
        this.playerJoinHandler.onJoin(playerName, playerUUID, false);
    }
}
