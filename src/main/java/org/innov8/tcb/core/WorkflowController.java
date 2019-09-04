package org.innov8.tcb.core;

import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The real class to implement WorkflowService
 * Created by wangqi on 2019/9/3.
 */
public class WorkflowController implements WorkflowService {
    private ConcurrentMap<String, Context> workspace = new ConcurrentHashMap<>();

    private static volatile WorkflowController instance;
    private WorkflowController() {}

    public static WorkflowController getInstance() {
        if (instance == null) {
            synchronized (WorkflowController.class) {
                if (instance == null) {
                    instance = new WorkflowController();
                }
            }
        }
        return instance;
    }

    public String startWorkflow(String flowType, Map<String, Object> metadata) {
        return null;
    }

    @Override
    public Pair<String, String> nextState(String contextId, String transition) {
        return null;
    }

    public class Context {
        String contextId;
        String currentStateId;
        String conversationIndex;
    }
}
