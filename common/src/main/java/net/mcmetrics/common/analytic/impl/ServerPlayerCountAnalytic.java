package net.mcmetrics.common.analytic.impl;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the number of players currently online on the server.
 */
@Data
public class ServerPlayerCountAnalytic implements NamedAnalytic {

    private final @NotNull String instanceReference;
    private final @NotNull Integer bedrockPlayerCount;
    private final @NotNull Integer javaPlayerCount;

    @Override
    public @NotNull String getEventType() {
        return "server_player_count";
    }
}
