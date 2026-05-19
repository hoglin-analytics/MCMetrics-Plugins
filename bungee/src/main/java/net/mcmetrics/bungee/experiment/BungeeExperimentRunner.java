package net.mcmetrics.bungee.experiment;

import net.mcmetrics.common.experiment.ExperimentRunner;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.UUID;

public class BungeeExperimentRunner implements ExperimentRunner {

    @Override
    public void dispatchCommandPlayer(UUID playerUUID, String cmd) {
        ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(playerUUID);
        ProxyServer.getInstance().getPluginManager().dispatchCommand(proxiedPlayer, cmd);
    }

    @Override
    public void dispatchCommandConsole(String cmd) {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), cmd);
    }

    @Override
    public void sendMessage(UUID playerUUID, String message) {
        ProxyServer.getInstance().getPlayer(playerUUID).sendMessage(message);
    }
}
