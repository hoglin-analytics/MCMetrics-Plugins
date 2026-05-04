package net.mcmetrics.fabric.listener;

import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerChatHandler;
import net.mcmetrics.fabric.Listener;
import net.mcmetrics.fabric.event.PlayerChatCallback;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatListener implements Listener {

    private final PlayerChatHandler playerChatHandler;

    public PlayerChatListener(MCMetrics mcMetrics) {
        this.playerChatHandler = new PlayerChatHandler(mcMetrics);
    }

    @Override
    public void register() {
        PlayerChatCallback.EVENT.register(this::onChat);
    }

    public void onChat(ServerboundChatPacket packet, ServerPlayer player) {
        String message = packet.message();
        this.playerChatHandler.onChat(player.getUUID(), message);
    }
}
