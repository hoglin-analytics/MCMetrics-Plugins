package net.mcmetrics.common.task;

import gg.hoglin.sdk.Hoglin;
import net.mcmetrics.common.MCMetrics;
import net.mcmetrics.common.connection.ConnectionManager;

public class ServerHeartbeatTask implements Runnable {

    private final MCMetrics mcMetrics;

    public ServerHeartbeatTask(final MCMetrics mcMetrics) {
        this.mcMetrics = mcMetrics;
    }

    @Override
    public void run() {
        final Hoglin hoglin = mcMetrics.getHoglin();
        if (hoglin == null) {
            return;
        }

        ConnectionManager connectionManager = mcMetrics.getConnectionManager();
        connectionManager.updateTPS(mcMetrics.getTpsSupplier().get(),  mcMetrics.getMsptSupplier().get());
        connectionManager.pushPerformanceUpdate();
        connectionManager.pushPlayerCountUpdate();
    }
}
