package net.mcmetrics.common.config.impl;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public class HoglinConfig {
    @SerializedName("server_key")
    private String serverKey;

    @SerializedName("auto_flush_interval")
    private long autoFlushInterval;

    @SerializedName("auto_flush_max_batch_size")
    private int autoFlushMaxBatchSize;

    @SerializedName("api_server")
    private String apiServerUrl;
}
