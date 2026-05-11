package net.mcmetrics.bukkit.util;

import net.mcmetrics.common.util.DoubleCircularBuffer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NMSUtils {

    private static final Logger LOGGER = Logger.getLogger(NMSUtils.class.getName());

    // Minecraft server
    private static Class<?> MINECRAFT_SERVER_CLASS;
    private static Method MINECRAFT_SERVER_GET_SERVER;
    private static Method MINECRAFT_SERVER_GET_TICK_RATE_MANAGER;
    private static Field MINECRAFT_SERVER_RECENT_TPS;

    // Tick rate manager
    private static Class<?> SERVER_TICK_RATE_MANAGER_CLASS;
    private static Method TICK_RATE_MANAGER_GET_NANOSECONDS_PER_TICK;

    private static final DoubleCircularBuffer circularBuffer = new DoubleCircularBuffer(20 * 60);

    static {
        try {
            // Minecraft server
            MINECRAFT_SERVER_CLASS = Class.forName("net.minecraft.server.MinecraftServer");
            MINECRAFT_SERVER_GET_SERVER = MINECRAFT_SERVER_CLASS.getDeclaredMethod("getServer"); // Technically this is deprecated but screw it
            MINECRAFT_SERVER_RECENT_TPS = MINECRAFT_SERVER_CLASS.getDeclaredField("recentTps");
            for (Method m : MINECRAFT_SERVER_CLASS.getDeclaredMethods()) {
                // Names are obfuscated in spigot but not in paper
                if (m.getReturnType() == SERVER_TICK_RATE_MANAGER_CLASS) {
                    MINECRAFT_SERVER_GET_TICK_RATE_MANAGER = m;
                    break;
                }
            }

            // Tick rate manager
            SERVER_TICK_RATE_MANAGER_CLASS = Class.forName("net.minecraft.server.ServerTickRateManager");
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

    public static double getTPS() {
        try {
            Object server = MINECRAFT_SERVER_GET_SERVER.invoke(null);
            double[] recentTps = (double[]) MINECRAFT_SERVER_RECENT_TPS.get(server);
            return recentTps[0];
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.log(Level.SEVERE, "Failed to get recent TPS", e);
            return -1.0;
        }
    }

    public static double getMSPT() {
        return circularBuffer.average();
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