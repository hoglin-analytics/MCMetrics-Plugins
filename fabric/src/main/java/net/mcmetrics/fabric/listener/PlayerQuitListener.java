package net.mcmetrics.fabric.listener;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerQuitAnalytic;
import net.mcmetrics.common.listener.PlayerQuitHandler;
import net.mcmetrics.common.player.TrackedPlayer;
import net.mcmetrics.fabric.Listener;
import net.mcmetrics.fabric.MCMetricsMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.apache.logging.log4j.Level;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PlayerQuitHandler playerQuitHandler;

    public PlayerQuitListener(MCMetrics mcMetrics) {
        this.playerQuitHandler = new PlayerQuitHandler(mcMetrics);
    }

    @Override
    public void register() {
        ServerPlayConnectionEvents.DISCONNECT.register(this::onQuit);
    }

    public void onQuit(ServerGamePacketListenerImpl handler, MinecraftServer server) {
        final UUID uuid = handler.getPlayer().getUUID();
        this.playerQuitHandler.onQuit(uuid);
    }
}
