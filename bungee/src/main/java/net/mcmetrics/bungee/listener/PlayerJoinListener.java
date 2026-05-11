package net.mcmetrics.bungee.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final PlayerJoinHandler playerJoinHandler;

    public PlayerJoinListener(final PlayerJoinHandler playerJoinHandler) {
        this.playerJoinHandler = playerJoinHandler;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin(final LoginEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final UUID uuid = event.getConnection().getUniqueId();
        final String ipAddress = event.getConnection().getAddress().getAddress().getHostAddress();
        final String hostName = event.getConnection().getVirtualHost().getHostName();

        this.playerJoinHandler.onLogin(uuid, ipAddress, hostName);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(final PostLoginEvent event) {
        String playerName = event.getPlayer().getName();
        UUID uuid = event.getPlayer().getUniqueId();

        // Bungee doesn't have a way to check new players, maybe we implement our own solution
        this.playerJoinHandler.onJoin(playerName, uuid, false);
    }
}
