package net.mcmetrics.bukkit.experiment;

import gg.hoglin.sdk.Hoglin;
import gg.hoglin.sdk.models.experiment.ExperimentData;
import gg.hoglin.sdk.models.experiment.ExperimentVariant;
import net.mcmetrics.bukkit.FoliaUtils;
import net.mcmetrics.bukkit.MCMetrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExperimentUtil {

    /**
     * Triggers the experiment to run for a player
     *
     * @param hoglin Hoglin instance
     * @param data cached experiment data object
     * @param player the player to run the experiment on
     */
    public static void triggerExperiment(Hoglin hoglin, ExperimentData data, Player player) {
        boolean exposed = hoglin.evaluateExperiment(data.getExperimentId(), player.getUniqueId());
        ExperimentVariant variant;
        String payload;
        if (exposed) {
            variant = data.getVariants().get(ExperimentVariant.Variant.EXPOSED);
            payload = variant.formatPayload(player.getName(), player.getUniqueId(), ExperimentVariant.Variant.EXPOSED);
        } else {
            variant = data.getVariants().get(ExperimentVariant.Variant.CONTROL);
            payload = variant.formatPayload(player.getName(), player.getUniqueId(), ExperimentVariant.Variant.CONTROL);
        }
        triggerAction(variant.getAction(), player, payload);
    }

    /**
     * Triggers the specific action associated with an experiment variant
     *
     * @param action the specific action
     * @param player the player that the action is being performed on
     * @param payload the payload used by the action
     */
    public static void triggerAction(ExperimentVariant.Action action, Player player, String payload) {
        if (FoliaUtils.isFolia()) {
            switch (action) {
                case RUN_CONSOLE_COMMAND -> Bukkit.getGlobalRegionScheduler().run(MCMetrics.getInstance(), task ->
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), payload));
                case RUN_COMMAND_AS_PLAYER -> player.getScheduler().run(MCMetrics.getInstance(), task ->
                        Bukkit.dispatchCommand(player, payload), null);
                case SEND_MESSAGE -> player.getScheduler().run(MCMetrics.getInstance(), task ->
                        player.sendMessage(payload), null);
            }
        } else {
            switch (action) {
                case RUN_CONSOLE_COMMAND -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), payload);
                case RUN_COMMAND_AS_PLAYER -> Bukkit.dispatchCommand(player, payload);
                case SEND_MESSAGE -> player.sendMessage(payload);
            }
        }
    }
}
