package net.mcmetrics.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerQuitHandler;

import java.util.UUID;

public class PlayerQuitListener {

    private final PlayerQuitHandler playerQuitHandler;

    public PlayerQuitListener(MCMetrics mcMetrics) {
        this.playerQuitHandler = new PlayerQuitHandler(mcMetrics);
    }

    @Subscribe
    public void onPlayerQuit(DisconnectEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        this.playerQuitHandler.onQuit(playerUUID);
    }
}
