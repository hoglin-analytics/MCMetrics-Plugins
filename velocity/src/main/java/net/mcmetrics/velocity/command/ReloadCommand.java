package net.mcmetrics.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ConsoleCommandSource;
import net.kyori.adventure.text.Component;
import net.mcmetrics.velocity.MCMetrics;
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
    public void reload(final CommandSource sender) {
        final long now = System.currentTimeMillis();
        final boolean success = MCMetrics.getInstance().attemptReload();

        if (success) {
            sender.sendMessage(Component.text("§aMCMetrics configuration reloaded successfully in " + (System.currentTimeMillis() - now) + "ms."));
            return;
        }

        if (sender instanceof ConsoleCommandSource) {
            sender.sendMessage(Component.text("§cFailed to reload MCMetrics configuration."));
        } else {
            sender.sendMessage(Component.text("§cFailed to reload MCMetrics configuration. Please check server console for details."));
        }
    }

}
