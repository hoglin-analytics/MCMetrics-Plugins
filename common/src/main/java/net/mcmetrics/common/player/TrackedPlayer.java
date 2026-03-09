package net.mcmetrics.common.player;

import lombok.Getter;
import lombok.Setter;
import net.mcmetrics.common.platform.ClientPlatform;
import org.jetbrains.annotations.NotNull;

@Getter
public class TrackedPlayer {

    @Setter private @NotNull String sessionId;
    @Setter private @NotNull String hostName;
    @Setter private @NotNull String ip;
    @Setter private @NotNull ClientPlatform clientPlatform;
    @Setter private long sessionStart;

}
