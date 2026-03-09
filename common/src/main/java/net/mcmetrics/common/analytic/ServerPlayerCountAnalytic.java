package net.mcmetrics.common.analytic;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the number of players currently online on the server.
 */
@Data
public class ServerPlayerCountAnalytic implements NamedAnalytic {

    private final @NotNull String instance;
    private final @NotNull Integer javaPlayerCount;
    private final @NotNull Integer bedrockPlayerCount;
    private final @NotNull Integer totalPlayerCount;

    @Override
    public @NotNull String getEventType() {
        return "server_player_count";
    }
}
