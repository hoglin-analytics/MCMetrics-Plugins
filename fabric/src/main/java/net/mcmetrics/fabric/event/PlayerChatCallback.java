package net.mcmetrics.fabric.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.protocol.game.ServerboundChatPacket;
import net.minecraft.server.level.ServerPlayer;

public interface PlayerChatCallback {

    Event<PlayerChatCallback> EVENT = EventFactory.createArrayBacked(PlayerChatCallback.class,
            (listeners) -> (packet, player) -> {
                for (PlayerChatCallback event : listeners) {
                    event.interact(packet, player);
                }
            });

    void interact(ServerboundChatPacket packet, ServerPlayer player);
}
