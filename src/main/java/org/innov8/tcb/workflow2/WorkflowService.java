package org.innov8.tcb.workflow2;


import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * This class is used to manage the flow for each session.
 *
 * Created by wangqi on 2019/9/4.
 */
public interface WorkflowService
{
    /**
     * Initialize a workflow context
     *
     * @param flowName the flow type to initialize
     * @param metadata the runtime data required to kick off this specific flow. It must contain all placeholder values in the corresponding workflow config.
     * @return a unique context ID of the running workflow
     */
    String initializeWorkflow(String flowName, Map<String, Object> metadata);

    /**
     * Initialize a workflow at specific state context
     *
     * @param flowName the flow type to initialize
     * @param nextState the first state should be start
     * @param metadata the runtime data required to kick off this specific flow. It must contain all placeholder values in the corresponding workflow config.
     * @return a unique context ID of the running workflow
     */
    String initializeWorkflow(String flowName, String nextState, Map<String, ?> metadata);

    /**
     * Moves to next workflow state by the given transition
     *
     * @param contextId  the context ID of the running workflow to manipulate on
     * @param condition the transition which determines the next state
     * @return A tuple of the next question and whom to ask
     */
    Pair<String, String> nextStep(String contextId, String condition);

    /**
     * Get Lex next entry after ReadyForFulfillment
     * @param flowName the flow type to initialize
     * @return A tuple of the next entry after ReadyForFulfillment
     *         key is the FIFO queue of questions, the questions should be in sequence
     *         value is the current state name(id)
     */
    Pair<List<String>, String> getLexFulfillmentEntry(String flowName);
}
