package net.mcmetrics.fabric.event;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.Connection;

public interface PlayerLoginCallback {

    Event<PlayerLoginCallback> EVENT = EventFactory.createArrayBacked(PlayerLoginCallback.class,
            (listeners) -> (gameProfile, connection, hostName) -> {
        for (PlayerLoginCallback event : listeners) {
            event.interact(gameProfile, connection, hostName);
        }
    });

    void interact(GameProfile gameProfile, Connection connection, String hostName);
}
