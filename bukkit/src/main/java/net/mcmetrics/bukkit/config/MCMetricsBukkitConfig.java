package net.mcmetrics.bukkit.config;

import lombok.Getter;
import lombok.experimental.Accessors;
import net.mcmetrics.common.config.impl.HoglinConfig;
import net.mcmetrics.common.config.impl.InstanceConfig;

@Getter
@Accessors(fluent = true)
public class MCMetricsBukkitConfig {
    private HoglinConfig hoglin;
    private InstanceConfig instance;
}
