package net.mcmetrics.bukkit.experiment;

import net.mcmetrics.bukkit.util.FoliaUtils;
import net.mcmetrics.common.experiment.ExperimentRunner;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

import static net.mcmetrics.bukkit.util.FoliaUtils.IS_FOLIA;

public class BukkitExperimentRunner implements ExperimentRunner {

    @Override
    public void dispatchCommandPlayer(UUID playerUUID, String cmd) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        if (IS_FOLIA) {
            FoliaUtils.runEntity(player, () -> Bukkit.dispatchCommand(player, cmd), null);
        } else {
            Bukkit.dispatchCommand(player, cmd);
        }
    }

    @Override
    public void dispatchCommandConsole(String cmd) {
        if (IS_FOLIA) {
            FoliaUtils.runGlobal(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    }

    @Override
    public void sendMessage(UUID playerUUID, String message) {
        Player player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        if (IS_FOLIA) {
            FoliaUtils.runEntity(player, () -> player.sendMessage(message), null);
        } else {
            player.sendMessage(message);
        }
    }
}
