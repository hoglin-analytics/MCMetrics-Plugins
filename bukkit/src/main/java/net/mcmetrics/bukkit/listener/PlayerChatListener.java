package net.mcmetrics.bukkit.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerChatAnalytic;
import net.mcmetrics.common.listener.PlayerChatHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final PlayerChatHandler playerChatHandler;

    public PlayerChatListener(final MCMetrics mcMetrics) {
        this.playerChatHandler = new PlayerChatHandler(mcMetrics);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.playerChatHandler.onChat(event.getPlayer().getUniqueId(), event.getMessage());
    }
}
