package net.mcmetrics.bukkit;

// https://docs.papermc.io/paper/dev/folia-support/
public class FoliaUtils {

    private static Boolean isFolia;

    public static boolean isFolia() {
        if (isFolia == null) {
            try {
                Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
                isFolia = true;
            } catch (ClassNotFoundException e) {
                isFolia = false;
            }
        }
        return isFolia;
    }
}
