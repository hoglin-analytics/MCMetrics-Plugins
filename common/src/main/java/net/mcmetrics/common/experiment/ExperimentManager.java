package net.mcmetrics.common.experiment;

import gg.hoglin.sdk.Hoglin;
import gg.hoglin.sdk.models.experiment.ExperimentData;
import gg.hoglin.sdk.models.experiment.ExperimentVariant;

import java.util.UUID;

public class ExperimentManager {

    private final ExperimentRunner runner;

    public ExperimentManager(ExperimentRunner runner) {
        this.runner = runner;
    }

    /**
     * Triggers the experiment to run for a player
     *
     * @param hoglin Hoglin instance
     * @param data cached experiment data object
     * @param playerName name of the player
     * @param playerUUID UUID of the player
     */
    public void triggerExperiment(Hoglin hoglin, ExperimentData data, String playerName, UUID playerUUID) {
        boolean exposed = hoglin.evaluateExperiment(data.getExperimentId(), playerUUID);
        ExperimentVariant variant;
        String payload;
        if (exposed) {
            variant = data.getVariants().get(ExperimentVariant.Variant.EXPOSED);
            payload = variant.formatPayload(playerName, playerUUID, ExperimentVariant.Variant.EXPOSED);
        } else {
            variant = data.getVariants().get(ExperimentVariant.Variant.CONTROL);
            payload = variant.formatPayload(playerName, playerUUID, ExperimentVariant.Variant.CONTROL);
        }
        triggerAction(variant.getAction(), playerUUID, payload);
    }

    /**
     * Triggers the specific action associated with an experiment variant
     *
     * @param action the specific action
     * @param playerUUID the UUID of the player that the action is being performed on
     * @param payload the payload used by the action
     */
    public void triggerAction(ExperimentVariant.Action action, UUID playerUUID, String payload) {
        switch (action) {
            case RUN_CONSOLE_COMMAND -> this.runner.dispatchCommandConsole(payload);
            case RUN_COMMAND_AS_PLAYER -> this.runner.dispatchCommandPlayer(playerUUID, payload);
            case SEND_MESSAGE -> this.runner.sendMessage(playerUUID, payload);
        }
    }
}
