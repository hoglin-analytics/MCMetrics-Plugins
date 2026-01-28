package net.mcmetrics.common.platform;

import java.util.UUID;

public class PlatformUtil {

    /**
     * Check if a UUID is a Bedrock edition UUID.
     *
     * @param uuid The UUID to check.
     * @return True if the UUID is a Bedrock edition UUID, false otherwise.
     */
    public static boolean isBedrock(final UUID uuid) {
        return uuid.version() == 0 && uuid.toString().startsWith("00000000-0000-0000");
    }

    /**
     * Get the client platform based on the UUID.
     *
     * @param uuid The UUID to check.
     * @return The ClientPlatform representing either Java or Bedrock.
     */
    public static ClientPlatform getPlatform(final UUID uuid) {
        return isBedrock(uuid) ? ClientPlatform.BEDROCK : ClientPlatform.JAVA;
    }

}
