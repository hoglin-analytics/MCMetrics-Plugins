package net.mcmetrics.common.analytic.player;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player makes a purchase on the server.
 */
@Data
public class PlayerPurchaseAnalytic implements NamedAnalytic {

    private final @NotNull String instanceReference;
    private final @NotNull UUID playerUUID;
    private final @NotNull String productName;
    private final @NotNull String currency;
    private final @NotNull Float purchaseValue;

    @Override
    public @NotNull String getEventType() {
        return "player_purchase";
    }
}
