package net.mcmetrics.common.analytic.impl;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player quits the server.
 */
@Data
public class PlayerQuitAnalytic implements NamedAnalytic {

    private final @NotNull String instanceReference;
    private final @NotNull UUID playerUUID;

    @Override
    public @NotNull String getEventType() {
        return "player_quit";
    }
}
