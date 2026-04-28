package net.mcmetrics.fabric.listener;

import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.mcmetrics.common.analytic.player.PlayerChatAnalytic;
import net.mcmetrics.fabric.MCMetrics;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatListener {

    private final MCMetrics mcMetrics;

    public PlayerChatListener(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;

        ServerMessageEvents.CHAT_MESSAGE.register(this::onChat);
    }

    public void onChat(PlayerChatMessage message, ServerPlayer player, ChatType.Bound type) {
        mcMetrics.getHoglin().track(new PlayerChatAnalytic(
                mcMetrics.getMcMetricsConfig().instance().id(),
                player.getUUID(),
                message.unsignedContent() != null ? message.unsignedContent().getString() : message.signedContent(),
                false
        ));
    }
}
