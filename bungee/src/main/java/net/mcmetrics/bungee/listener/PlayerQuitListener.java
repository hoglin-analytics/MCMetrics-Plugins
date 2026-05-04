package net.mcmetrics.bungee.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerQuitHandler;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PlayerQuitHandler playerQuitHandler;

    public PlayerQuitListener(MCMetrics mcMetrics) {
        this.playerQuitHandler = new PlayerQuitHandler(mcMetrics);
    }

    @EventHandler
    public void onQuit(final PlayerDisconnectEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        this.playerQuitHandler.onQuit(uuid);
    }
}
