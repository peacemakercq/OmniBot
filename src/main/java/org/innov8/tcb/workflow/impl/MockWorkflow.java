package org.innov8.tcb.workflow.impl;

import javafx.util.Pair;
import org.innov8.tcb.workflow.Workflow;

import java.util.Map;
import java.util.Queue;

public class MockWorkflow implements Workflow
{
    @Override
    public String startWorkflow(String flowType, Map<String, ?> metadata)
    {
        return null;
    }

    @Override
    public String startWorkflow(String flowType, String nextState, Map<String, ?> metadata)
    {
        return null;
    }

    @Override
    public Pair<String, String> nextState(String contextId, String transition)
    {
        return null;
    }

    @Override
    public Pair<Queue<String>, String> getLexFulfillmentEntry(String flowType)
    {
        //Pair<Queue<String>, String> questionAndAnswer = new Pair<>();
        return null;
    }
}
