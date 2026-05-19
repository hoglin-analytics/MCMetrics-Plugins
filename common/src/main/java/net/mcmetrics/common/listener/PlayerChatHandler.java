package net.mcmetrics.common.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerChatAnalytic;

import java.util.UUID;

public class PlayerChatHandler {

    private final MCMetrics mcMetrics;

    public PlayerChatHandler(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    public void onChat(UUID playerUUID, String message) {
        mcMetrics.getHoglin().track(new PlayerChatAnalytic(
                mcMetrics.getConfig().instance().id(),
                playerUUID,
                message,
                false // default to false until we implement support with existing chat filters or implement our own
        ));
    }
}
