package net.mcmetrics.fabric.experiment;

import net.mcmetrics.common.experiment.ExperimentRunner;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class FabricExperimentRunner implements ExperimentRunner {

    private final MinecraftServer server;

    public FabricExperimentRunner(MinecraftServer server) {
        this.server = server;
    }

    @Override
    public void dispatchCommandPlayer(UUID playerUUID, String cmd) {
        Player player = this.server.getPlayerList().getPlayer(playerUUID);
        if (player == null) return;

        this.server.getCommands().performPrefixedCommand(
                player.createCommandSourceStack(),
                cmd
        );
    }

    @Override
    public void dispatchCommandConsole(String cmd) {
        server.getCommands().performPrefixedCommand(
                server.createCommandSourceStack(),
                cmd
        );
    }

    @Override
    public void sendMessage(UUID playerUUID, String message) {
        Player player = this.server.getPlayerList().getPlayer(playerUUID);
        if (player == null) return;

        player.sendSystemMessage(Component.literal(message));
    }
}
