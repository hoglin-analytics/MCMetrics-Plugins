package net.mcmetrics.bukkit.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.mcmetrics.common.config.impl.HoglinConfig;

@Getter
@Accessors(fluent = true)
public class MCMetricsBukkitConfig {
    private HoglinConfig hoglin;
}
