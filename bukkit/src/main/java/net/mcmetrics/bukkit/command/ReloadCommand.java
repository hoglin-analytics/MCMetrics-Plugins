package net.mcmetrics.bukkit.command;

import net.mcmetrics.bukkit.MCMetrics;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

/**
 * Command to reload the Hoglin configuration.
 */
public class ReloadCommand {

    @Command("mcmetrics reload")
    @CommandDescription("Reloads the MCMetrics configuration.")
    @Permission("mcmetrics.reload")
    public void reload(final CommandSender sender) {
        final long now = System.currentTimeMillis();
        final boolean success = MCMetrics.getInstance().attemptReload();

        if (success) {
            sender.sendMessage("§aMCMetrics configuration reloaded successfully in " + (System.currentTimeMillis() - now) + "ms.");
            return;
        }

        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("§cFailed to reload MCMetrics configuration.");
        } else {
            sender.sendMessage("§cFailed to reload MCMetrics configuration. Please check server console for details.");
        }
    }

}
