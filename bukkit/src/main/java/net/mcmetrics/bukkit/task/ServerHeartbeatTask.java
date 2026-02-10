package net.mcmetrics.bukkit.task;

import gg.hoglin.sdk.Hoglin;
import net.mcmetrics.bukkit.MCMetrics;

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
