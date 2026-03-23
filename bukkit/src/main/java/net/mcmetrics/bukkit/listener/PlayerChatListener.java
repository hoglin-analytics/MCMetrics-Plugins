package net.mcmetrics.bukkit.listener;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerChatAnalytic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerChatListener implements Listener {

    private final MCMetrics mcMetrics;

    public PlayerChatListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        mcMetrics.getHoglin().track(new PlayerChatAnalytic(
            mcMetrics.getMcMetricsConfig().instance().id(),
            event.getPlayer().getUniqueId(),
                event.getMessage(),
                false, false, false, false, false
        ));
    }

}
