package org.innov8.tcb.workflow;

import javafx.util.Pair;

import java.util.Map;

public interface Workflow {
    /**
     * Initilaizes a workflow context
     *
     * @param flowType the flow type to initialize
     * @param metadata the runtime data required to kick off this specific flow. It must contain all placeholder values in the corresponding workflow config.
     * @return a unique context ID of the running workflow
     */
    String startWorkflow(String flowType, Map<String, Object> metadata);

    /**
     * Moves to next workflow state by the given transition
     *
     * @param contextId  the context ID of the running workflow to manipulate on
     * @param transition the transition which determines the next state
     * @return A tuple of the next question and whom to ask
     */
    Pair<String, String> nextState(String contextId, String transition);
}
