package net.mcmetrics.common.analytic.player;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Represents all the data collected when a player sends a chat message.
 */
@Data
public class PlayerChatAnalytic implements NamedAnalytic {

    private final @NotNull String instance;
    private final @NotNull UUID playerUUID;
    private final @NotNull String message;
    private final boolean toxic;

    @Override
    public @NotNull String getEventType() {
        return "player_chat";
    }
}

