package net.mcmetrics.common.config.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@ToString
public class HoglinConfig {

    @JsonProperty("server_key")
    private String serverKey;

    @JsonProperty("auto_flush_interval")
    private long autoFlushInterval;

    @JsonProperty("auto_flush_max_batch_size")
    private int autoFlushMaxBatchSize;

    @JsonProperty("api_server")
    private String apiServerUrl;
}
