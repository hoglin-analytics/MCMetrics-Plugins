package net.mcmetrics.common.analytic;

import gg.hoglin.sdk.models.analytic.NamedAnalytic;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

@Data
public class ServerPingAnalytic implements NamedAnalytic {
    private final @NotNull Integer javaPlayerCount;
    private final @NotNull Integer bedrockPlayerCount;

    @Override
    public @NotNull String getEventType() {
        return "server_ping";
    }
}
