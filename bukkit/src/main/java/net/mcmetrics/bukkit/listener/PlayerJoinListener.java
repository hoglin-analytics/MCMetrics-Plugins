package net.mcmetrics.bukkit.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final PlayerJoinHandler playerJoinHandler;

    public PlayerJoinListener(final MCMetrics mcMetrics) {
        this.playerJoinHandler = new PlayerJoinHandler(mcMetrics);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogin(final PlayerLoginEvent event) {
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            return;
        }

        final UUID uuid = event.getPlayer().getUniqueId();
        final String ipAddress = event.getAddress().getHostAddress();
        final String hostName = event.getHostname();

        this.playerJoinHandler.onLogin(uuid, ipAddress, hostName);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        playerJoinHandler.onJoin(player.getName(), player.getUniqueId(), !player.hasPlayedBefore());
    }
}
