package net.mcmetrics.common.util;

import java.util.UUID;

public class BedrockUtil {

    /**
     * Check if a UUID is a Bedrock edition UUID.
     *
     * @param uuid The UUID to check.
     * @return True if the UUID is a Bedrock edition UUID, false otherwise.
     */
    public static boolean isBedrock(UUID uuid) {
        return uuid.version() == 0 && uuid.toString().startsWith("00000000-0000-0000");
    }

}
