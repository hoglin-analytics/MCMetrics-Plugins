package net.mcmetrics.bukkit;

// https://docs.papermc.io/paper/dev/folia-support/
public class FoliaUtils {

    public static boolean isFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
