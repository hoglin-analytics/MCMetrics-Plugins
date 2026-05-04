package net.mcmetrics.bukkit.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerQuitHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerQuitHandler playerQuitHandler;

    public PlayerQuitListener(final MCMetrics mcMetrics) {
        this.playerQuitHandler = new PlayerQuitHandler(mcMetrics);
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        playerQuitHandler.onQuit(event.getPlayer().getUniqueId());
    }
}
