package net.mcmetrics.bukkit.command;

import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.player.PlayerPurchaseAnalytic;
import org.bukkit.command.ConsoleCommandSender;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.UUID;

/**
 * Command to track player purchases on the server. This command should be set up to be executed by the webstore
 * platform when a player makes a purchase.
 */
public class TrackPurchaseCommand {

    @Command("playertracker purchase <player_uuid> <purchase_value> <currency> <product_name>")
    @Permission("*")
    public void trackPurchase(
        final ConsoleCommandSender sender,
        @Argument("player_uuid") final UUID playerUuid,
        @Argument("purchase_value") @Range(min = "0") final float purchaseValue,
        @Argument("currency") final String currency,
        @Argument("product_name") @Greedy final String productName
    ) {

        if (!MCMetrics.getInstance().getHoglinLoader().isLoaded()) {
            sender.sendMessage("§cAttempted to track a purchase whilst MCMetrics is correctly configured, this analytic will be lost. Please ensure the plugin is configured correctly.");
            return;
        }

        MCMetrics.getInstance().getHoglin().track(
            new PlayerPurchaseAnalytic(
                    MCMetrics.getInstance().getMcMetricsConfig().instance().reference(),
                    playerUuid,
                    productName,
                    currency,
                    purchaseValue
            )
        );
    }

}
