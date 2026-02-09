package net.mcmetrics.common.analytic;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a server heartbeat, primarily used for tracking server uptime
 */
@Data
public class ServerHeartbeatAnalytic implements NamedAnalytic {

    @Override
    public @NotNull String getEventType() {
        return "server_heartbeat";
    }
}
