package org.innov8.tcb.core;

import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.innov8.tcb.core.conversation.Conversation;
import org.innov8.tcb.core.conversation.ConversationManager;
import org.innov8.tcb.core.state.State;
import org.innov8.tcb.core.state.StateManager;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The real class to implement WorkflowService
 * Created by wangqi on 2019/9/3.
 */
public class WorkflowController implements WorkflowService {
    private Logger logger = LogManager.getLogger();
    private ConcurrentMap<String, Context> contextMap = new ConcurrentHashMap<>();
    private static int contextIdRef = 0;

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
        if (flowType == null || flowType.isEmpty()) {
            logger.error("Cannot create workflow for empty flowType!");
            return null;
        }
        State rootState = StateManager.getInstance().getRootState(flowType.concat(".puml"));
        if (rootState == null) {
            logger.error("Failed to create rootState for {}, flowType not found!", flowType);
            return null;
        }
        String contextId = flowType.concat("_").concat(String.valueOf(++contextIdRef));
        Context context = new Context(contextId, flowType, metadata);
        context.currentStateId = rootState.getId();
        context.conversation = ConversationManager.getInstance().getConversation(flowType, rootState.getId());
        contextMap.put(contextId, context);
        return contextId;
    }

    @Override
    public String startWorkflow(String flowType, String nextState, Map<String, ?> metadata)
    {
        return null;
    }

    @Override
    public Pair<String, String> nextState(String contextId, String response) {

        if (contextId == null || contextId.isEmpty() || ! contextMap.containsKey(contextId)) {
            logger.warn("The contextId {} does not exist! return null!", contextId);
            return null;
        }
        Context context = contextMap.get(contextId);
        context.addResponse(response);

        Pair<String, String> nextPair = context.getNext();
        if (nextPair != null) {
            return nextPair;
        }
        State nextState = StateManager.getInstance().getNext(context.flowType.concat(".puml"), context.currentStateId, response);
        if (nextState == null) {
            logger.info("[{}} No next state for <{},{}>", contextId, context.currentStateId, response);
            return null;
        }
        String nextStateId = nextState.getId();
        // update Context
        context.currentStateId = nextStateId;
        context.conversation = ConversationManager.getInstance().getConversation(context.flowType, nextStateId);
        return context.getNext();
    }

    @Override
    public org.apache.commons.lang3.tuple.Pair<Queue<String>, String> getLexFulfillmentEntry(String flowType)
    {
        return null;
    }

    public void printContextCacheForId(String contextId) {
        for (String key : contextMap.get(contextId).contextCache.keySet()) {
            logger.info("key: {}, entry: {}", key, contextMap.get(contextId).contextCache.get(key));
        }
    }

    private static int posInConversation = 0;
    public class Context {
        String contextId;
        String flowType;
        Map<String, Object> metadata;
        String currentStateId;
        ConcurrentMap<String, String> contextCache = new ConcurrentHashMap<>();
        Conversation conversation;

        Context(String contextId, String flowType, Map<String, Object> metadata) {
            this.contextId = contextId;
            this.flowType = flowType;
            this.metadata = metadata;
        }

        /**
         * Stores question and response into cache.
         * @param response response data.
         */
        void addResponse(String response) {
            if (conversation == null) {
                logger.warn("conversation is null, cannot add response: {}", response);
                return;
            }
            String index = (conversation.getQuestions().size() == 1) ? "" : "[" + posInConversation + "]";
            String questionKey = String.format("q.%s%s", currentStateId, index);
            String responseKey = String.format("a.%s%s", currentStateId, index);
            contextCache.put(questionKey, "" + conversation.getQuestions().get(posInConversation - 1));
            contextCache.put(responseKey, "" + response);
        }

        /**
         * Get next Question in conversation
         * @return Pair of <sendTo, question>
         */
        Pair<String, String> getNext() {
            if (conversation == null || conversation.getQuestions().size() <= posInConversation) {
                posInConversation = 0;
                return null;
            }
            String question = conversation.getQuestions().get(posInConversation++);
            return new Pair<>(conversation.getSendTo(), question);
        }
    }
}
