package net.mcmetrics.velocity.task;

import gg.hoglin.sdk.Hoglin;
import net.mcmetrics.velocity.MCMetrics;

public class ServerHeartbeatTask implements Runnable {

    @Override
    public void run() {
        final Hoglin hoglin = MCMetrics.getInstance().getHoglinLoader().getHoglin();
        if (hoglin == null) {
            return;
        }

        MCMetrics.getInstance().getConnectionManager().pushPlayerCountUpdate();
    }
}
