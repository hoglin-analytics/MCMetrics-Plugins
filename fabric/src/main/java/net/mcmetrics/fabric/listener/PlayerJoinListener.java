package net.mcmetrics.fabric.listener;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.listener.PlayerJoinHandler;
import net.mcmetrics.fabric.Listener;
import net.mcmetrics.fabric.event.PlayerLoginCallback;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.stats.Stats;

import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final PlayerJoinHandler playerJoinHandler;

    public PlayerJoinListener(MCMetrics mcMetrics) {
        this.playerJoinHandler = new PlayerJoinHandler(mcMetrics);
    }

    @Override
    public void register() {
        PlayerLoginCallback.EVENT.register(this::onLogin);
        ServerPlayConnectionEvents.JOIN.register(this::onJoin);
    }

    public void onLogin(GameProfile gameProfile, Connection connection, String hostName) {
        UUID uuid = gameProfile.getId();
        String ipAddress = connection.getRemoteAddress().toString();
        this.playerJoinHandler.onLogin(uuid, ipAddress, hostName);
    }

    public void onJoin(ServerGamePacketListenerImpl packet, PacketSender sender, MinecraftServer server) {
        ServerPlayer player = packet.getPlayer();
        int playTime = player.getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));

        String playerName = player.getName().getString();
        UUID uuid = player.getGameProfile().getId();
        boolean isNewPlayer = playTime == 0;

        this.playerJoinHandler.onJoin(playerName, uuid, isNewPlayer);
    }
}
