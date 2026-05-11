package net.mcmetrics.bukkit.util;

import net.mcmetrics.bukkit.MCMetricsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Rewrite of all necessary Folia method calls as reflections to avoid using Folia lib and in turn avoid Paper API.
 * To anyone who wants to contribute to this part (for whatever reason), please preface any methods with their
 * respective javadocs page.
 *
 * To any future maintainers, ideally keep this file at a minimum, defining only what actually gets used.
 */
public class FoliaUtils {

    public static final Logger LOGGER = Logger.getLogger(FoliaUtils.class.getName());

    public static final Boolean IS_FOLIA;

    // Global region scheduler
    public static Class<?> GLOBAL_REGION_SCHEDULER_CLASS;
    public static Method GET_GLOBAL_REGION_SCHEDULER;
    public static Method GLOBAL_SCHEDULER_RUN;
    public static Method GLOBAL_SCHEDULER_RUN_AT_FIXED_RATE;
    public static Method GLOBAL_SCHEDULER_CANCEL_TASKS;

    // Entity scheduler
    public static Class<?> ENTITY_SCHEDULER_CLASS;
    public static Method GET_ENTITY_SCHEDULER;
    public static Method ENTITY_SCHEDULER_RUN;


    static {
        // Check for Folia: https://docs.papermc.io/paper/dev/folia-support/
        boolean folia;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            folia = true;
        } catch (ClassNotFoundException e) {
            folia = false;
        }
        IS_FOLIA = folia;

        try {
            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/GlobalRegionScheduler.html
            GLOBAL_REGION_SCHEDULER_CLASS = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
            // https://jd.papermc.io/folia/1.21.11/org/bukkit/Server.html#getGlobalRegionScheduler()
            GET_GLOBAL_REGION_SCHEDULER = Server.class.getDeclaredMethod("getGlobalRegionScheduler");
            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/GlobalRegionScheduler.html#run(org.bukkit.plugin.Plugin,java.util.function.Consumer)
            GLOBAL_SCHEDULER_RUN = GLOBAL_REGION_SCHEDULER_CLASS.getDeclaredMethod(
                    "run", Plugin.class, Consumer.class);
            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/GlobalRegionScheduler.html#runAtFixedRate(org.bukkit.plugin.Plugin,java.util.function.Consumer,long,long)
            GLOBAL_SCHEDULER_RUN_AT_FIXED_RATE = GLOBAL_REGION_SCHEDULER_CLASS.getDeclaredMethod(
                    "runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/GlobalRegionScheduler.html#cancelTasks(org.bukkit.plugin.Plugin)
            GLOBAL_SCHEDULER_CANCEL_TASKS = GLOBAL_REGION_SCHEDULER_CLASS.getDeclaredMethod(
                    "cancelTasks", Plugin.class);

            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/EntityScheduler.html
            ENTITY_SCHEDULER_CLASS = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
            // https://jd.papermc.io/folia/1.21.11/org/bukkit/entity/Entity.html#getScheduler()
            GET_ENTITY_SCHEDULER = Entity.class.getDeclaredMethod("getScheduler");
            // https://jd.papermc.io/folia/1.21.11/io/papermc/paper/threadedregions/scheduler/EntityScheduler.html#run(org.bukkit.plugin.Plugin,java.util.function.Consumer,java.lang.Runnable)
            ENTITY_SCHEDULER_RUN = ENTITY_SCHEDULER_CLASS.getDeclaredMethod(
                    "run", Plugin.class, Consumer.class, Runnable.class);
        } catch (NoSuchMethodException | ClassNotFoundException e) {
            if (IS_FOLIA) {
                // If running Folia and this gets thrown, something is very very wrong
                LOGGER.log(Level.SEVERE, "Failed to initialize reflections for Folia", e);
            }
        }
    }

    public static void runGlobal(@NotNull Runnable task) {
        try {
            Consumer<Object> consumer = (ignore) -> task.run();
            Object scheduler = GET_GLOBAL_REGION_SCHEDULER.invoke(Bukkit.getServer());
            GLOBAL_SCHEDULER_RUN.invoke(scheduler, MCMetricsPlugin.getInstance(), consumer);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Failed to run global task", e);
        }
    }

    public static void scheduleGlobal(@NotNull Runnable task, long delay, long period) {
        try {
            Consumer<Object> consumer = (ignore) -> task.run();
            Object scheduler = GET_GLOBAL_REGION_SCHEDULER.invoke(Bukkit.getServer());
            GLOBAL_SCHEDULER_RUN_AT_FIXED_RATE.invoke(scheduler, MCMetricsPlugin.getInstance(), consumer, delay, period);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Failed to schedule global task", e);
        }
    }

    public static void cancelAllGlobalTasks() {
        try {
            Object scheduler = GET_GLOBAL_REGION_SCHEDULER.invoke(Bukkit.getServer());
            GLOBAL_SCHEDULER_CANCEL_TASKS.invoke(scheduler, MCMetricsPlugin.getInstance());
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Failed to cancel global tasks", e);
        }
    }

    public static void runEntity(@NotNull Entity entity, @NotNull Runnable task, @Nullable Runnable retired) {
        try {
            Consumer<Object> consumer = (ignore) -> task.run();
            Object scheduler = GET_ENTITY_SCHEDULER.invoke(entity);
            ENTITY_SCHEDULER_RUN.invoke(scheduler, MCMetricsPlugin.getInstance(), consumer, retired);
        } catch (IllegalAccessException | InvocationTargetException e) {
            LOGGER.log(Level.SEVERE, "Failed to run task on entity", e);
        }
    }
}
