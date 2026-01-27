package net.mcmetrics.common.analytic.player;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player joins the server.
 */
@Data
public class PlayerJoinAnalytic implements NamedAnalytic {

    private final @NotNull String instanceReference;
    private final @NotNull UUID playerUUID;
    private final @NotNull String hostName;
    private final @NotNull String ip;

    @Override
    public @NotNull String getEventType() {
        return "player_join";
    }
}

