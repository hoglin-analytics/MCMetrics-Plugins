package net.mcmetrics.common.experiment;

import java.util.UUID;

public interface ExperimentRunner {

    void dispatchCommandPlayer(UUID playerUUID, String cmd);

    void dispatchCommandConsole(String cmd);

    void sendMessage(UUID playerUUID, String message);
}
