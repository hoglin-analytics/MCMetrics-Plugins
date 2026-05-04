package net.mcmetrics.bukkit.experiment;

import net.mcmetrics.common.experiment.ExperimentRunner;
import org.bukkit.Bukkit;

import java.util.Objects;
import java.util.UUID;

public class BukkitExperimentRunner implements ExperimentRunner {

    @Override
    public void dispatchCommandPlayer(UUID playerUUID, String cmd) {
        Bukkit.dispatchCommand(Objects.requireNonNull(Bukkit.getPlayer(playerUUID)), cmd);
    }

    @Override
    public void dispatchCommandConsole(String cmd) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }

    @Override
    public void sendMessage(UUID playerUUID, String message) {
        Objects.requireNonNull(Bukkit.getPlayer(playerUUID)).sendMessage(message);
    }
}
