package net.mcmetrics.bukkit.util;

import net.mcmetrics.common.util.DoubleCircularBuffer;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NMSUtils {

    private static final Logger LOGGER = Logger.getLogger(NMSUtils.class.getName());

    public static final boolean IS_PAPER;

    // Minecraft server
    private static Class<?> MINECRAFT_SERVER_CLASS;
    private static Method MINECRAFT_SERVER_GET_SERVER;
    private static Method MINECRAFT_SERVER_GET_TICK_RATE_MANAGER;
    private static Field MINECRAFT_SERVER_RECENT_TPS;

    // Tick rate manager
    private static Class<?> SERVER_TICK_RATE_MANAGER_CLASS;
    private static Method TICK_RATE_MANAGER_GET_NANOSECONDS_PER_TICK;

    // Paper specific
    private static Method BUKKIT_GET_TPS;
    private static Method BUKKIT_GET_AVERAGE_TICK_TIME;

    private static final DoubleCircularBuffer circularBuffer = new DoubleCircularBuffer(20 * 60);

    static {
        boolean isPaper;
        try {
            // https://jd.papermc.io/paper/1.21.11/io/papermc/paper/ServerBuildInfo.html
            Class.forName("io.papermc.paper.ServerBuildInfo");
            isPaper = true;
        } catch (ClassNotFoundException e) {
            isPaper = false;
        }
        IS_PAPER = isPaper;

        if (IS_PAPER) {
            try {
                // Paper specific
                // https://jd.papermc.io/paper/1.21.11/org/bukkit/Bukkit.html#getTPS()
                BUKKIT_GET_TPS = Bukkit.class.getDeclaredMethod("getTPS");
                // https://jd.papermc.io/paper/1.21.11/org/bukkit/Bukkit.html#getAverageTickTime()
                BUKKIT_GET_AVERAGE_TICK_TIME = Bukkit.class.getDeclaredMethod("getAverageTickTime");
            } catch (NoSuchMethodException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize reflections for Paper", e);
            }
        } else {
            try {
                // Minecraft server
                MINECRAFT_SERVER_CLASS = Class.forName("net.minecraft.server.MinecraftServer");
                SERVER_TICK_RATE_MANAGER_CLASS = Class.forName("net.minecraft.server.ServerTickRateManager");
                MINECRAFT_SERVER_GET_SERVER = MINECRAFT_SERVER_CLASS.getDeclaredMethod("getServer"); // Technically this is deprecated but screw it
                MINECRAFT_SERVER_RECENT_TPS = MINECRAFT_SERVER_CLASS.getDeclaredField("recentTps");
                for (Method m : MINECRAFT_SERVER_CLASS.getDeclaredMethods()) {
                    // Names are obfuscated in spigot, fun times
                    if (m.getReturnType() == SERVER_TICK_RATE_MANAGER_CLASS) {
                        MINECRAFT_SERVER_GET_TICK_RATE_MANAGER = m;
                        break;
                    }
                }

                // Tick rate manager
                for (Method m : SERVER_TICK_RATE_MANAGER_CLASS.getMethods()) {
                    if (m.getReturnType() == long.class) {
                        TICK_RATE_MANAGER_GET_NANOSECONDS_PER_TICK = m;
                        break;
                    }
                }
            } catch (ClassNotFoundException | NoSuchMethodException | NoSuchFieldException e) {
                LOGGER.log(Level.SEVERE, "Failed to initialize reflections for NMS", e);
            }
        }
    }

    // If spigot, get recent tps, if paper, fall back to Bukkit#getTPS
    public static double getTPS() {
        if (IS_PAPER) {
            try {
                double[] tps = (double[]) BUKKIT_GET_TPS.invoke(null);
                return tps[0];
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.log(Level.SEVERE, "Failed to get recent TPS", e);
                return -1.0;
            }
        } else {
            try {
                Object server = MINECRAFT_SERVER_GET_SERVER.invoke(null);
                double[] recentTps = (double[]) MINECRAFT_SERVER_RECENT_TPS.get(server);
                return Math.min(20.0, recentTps[0]);
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.log(Level.SEVERE, "Failed to get recent TPS", e);
                return -1.0;
            }
        }
    }

    public static double getMSPT() {
        if (IS_PAPER) {
            try {
                double mspt = (double) BUKKIT_GET_AVERAGE_TICK_TIME.invoke(null);
                return mspt;
            } catch (InvocationTargetException | IllegalAccessException e) {
                LOGGER.log(Level.SEVERE, "Failed to get average tick time", e);
                return -1.0;
            }
        } else {
            return circularBuffer.average();
        }
    }

    public static void pushMSPT() {
        try {
            Object server = MINECRAFT_SERVER_GET_SERVER.invoke(null);
            Object tickManager = MINECRAFT_SERVER_GET_TICK_RATE_MANAGER.invoke(server);
            long nanosecondsPerTick = (long) TICK_RATE_MANAGER_GET_NANOSECONDS_PER_TICK.invoke(tickManager);
            double millisecondsPerTick = (double) nanosecondsPerTick / 1_000_000;
            circularBuffer.push(millisecondsPerTick);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}