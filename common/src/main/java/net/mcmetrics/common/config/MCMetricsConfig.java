package net.mcmetrics.common.config;

import com.fasterxml.jackson.annotation.JsonMerge;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;
import net.mcmetrics.common.config.impl.HoglinConfig;
import net.mcmetrics.common.config.impl.InstanceConfig;

@Getter
@Accessors(fluent = true)
@ToString
public class MCMetricsConfig {

    @JsonMerge
    private HoglinConfig hoglin;

    @JsonMerge
    private InstanceConfig instance;
}
