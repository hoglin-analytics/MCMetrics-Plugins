package net.mcmetrics.common.command;

import net.mcmetrics.common.MCMetrics;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

/**
 * Command to reload the Hoglin configuration.
 */
public class ReloadCommand {

    private final MCMetrics mcMetrics;

    public ReloadCommand(MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @Command("mcmetrics reload")
    @CommandDescription("Reloads the MCMetrics configuration.")
    @Permission("mcmetrics.reload")
    public void reload(final CommandSender<?> sender) {
        final long now = System.currentTimeMillis();
        final boolean success = mcMetrics.attemptReload();

        if (success) {
            sender.sendMessage("§aMCMetrics configuration reloaded successfully in " + (System.currentTimeMillis() - now) + "ms.");
            return;
        }

        if (sender.isConsole()) {
            sender.sendMessage("§cFailed to reload MCMetrics configuration.");
        } else {
            sender.sendMessage("§cFailed to reload MCMetrics configuration. Please check server console for details.");
        }
    }

}
