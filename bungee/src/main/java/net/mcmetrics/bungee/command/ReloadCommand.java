package net.mcmetrics.bungee.command;

import net.mcmetrics.bungee.MCMetrics;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ConnectedPlayer;
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

        if (sender instanceof ConnectedPlayer) {
            sender.sendMessage("§cFailed to reload MCMetrics configuration. Please check server console for details.");
        } else {
            sender.sendMessage("§cFailed to reload MCMetrics configuration.");
        }
    }

}
