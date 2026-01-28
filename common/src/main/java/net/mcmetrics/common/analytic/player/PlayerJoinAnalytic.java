package net.mcmetrics.common.analytic.player;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.mcmetrics.common.platform.ClientPlatform;
import net.mcmetrics.common.player.TrackedPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player joins the server.
 */
@Data
@AllArgsConstructor
public class PlayerJoinAnalytic implements NamedAnalytic {

    private final @NotNull String instance;
    private final @NotNull UUID playerUUID;
    private final @NotNull String hostName;
    private final @NotNull String ip;
    private final @NotNull ClientPlatform clientPlatform;

    public PlayerJoinAnalytic(
        final @NotNull String instance,
        final @NotNull UUID playerUUID,
        final @NotNull TrackedPlayer trackedPlayer
    ) {
        this.instance = instance;
        this.playerUUID = playerUUID;
        this.hostName = trackedPlayer.getHostName();
        this.ip = trackedPlayer.getIp();
        this.clientPlatform = trackedPlayer.getClientPlatform();
    }

    @Override
    public @NotNull String getEventType() {
        return "player_join";
    }
}

