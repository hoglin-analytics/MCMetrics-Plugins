package net.mcmetrics.common.analytic.player;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import net.mcmetrics.common.platform.ClientPlatform;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player quits the server.
 */
@Data
public class PlayerQuitAnalytic implements NamedAnalytic {

    private final @NotNull String instance;
    private final @NotNull String sessionId;
    private final @NotNull UUID playerUUID;

    // Until joins are implemented, data duplication is a necessary evil
    private final @NotNull String hostName;
    private final @NotNull String ip;
    private final @NotNull ClientPlatform clientPlatform;

    private final long sessionTime;

    @Override
    public @NotNull String getEventType() {
        return "player_quit";
    }
}
