package net.mcmetrics.velocity.experiment;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mcmetrics.common.experiment.ExperimentRunner;

import java.util.Optional;
import java.util.UUID;

public class VelocityExperimentRunner implements ExperimentRunner {

    private final ProxyServer server;

    public VelocityExperimentRunner(ProxyServer server) {
        this.server = server;
    }

    @Override
    public void dispatchCommandPlayer(UUID playerUUID, String cmd) {
        Optional<Player> source = server.getPlayer(playerUUID);
        if (source.isEmpty()) {
            // Well something went wrong
            return;
        }

        this.server.getCommandManager().executeAsync(source.get(), cmd);
    }

    @Override
    public void dispatchCommandConsole(String cmd) {
        this.server.getCommandManager().executeAsync(this.server.getConsoleCommandSource(), cmd);
    }

    @Override
    public void sendMessage(UUID playerUUID, String message) {
        Optional<Player> source = server.getPlayer(playerUUID);
        if (source.isEmpty()) {
            // Well something went wrong
            return;
        }

        source.get().sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }
}
