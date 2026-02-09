package net.mcmetrics.bukkit.runnable;

import gg.hoglin.sdk.Hoglin;
import net.mcmetrics.bukkit.MCMetrics;
import net.mcmetrics.common.analytic.ServerHeartbeatAnalytic;

public class ServerHeartbeatTask implements Runnable {

    @Override
    public void run() {
        final Hoglin hoglin = MCMetrics.getInstance().getHoglinLoader().getHoglin();
        if (hoglin == null) {
            return;
        }

        hoglin.track(new ServerHeartbeatAnalytic());
    }

}
