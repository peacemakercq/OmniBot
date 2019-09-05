package org.innov8.tcb.workflow;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.innov8.tcb.workflow.conversation.Conversation;
import org.innov8.tcb.workflow.conversation.ConversationManager;
import org.innov8.tcb.workflow.state.State;
import org.innov8.tcb.workflow.state.StateManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The real class to implement WorkflowService
 * Created by wangqi on 2019/9/3.
 */
public class WorkflowServiceImpl implements WorkflowService {
    private Logger logger = LogManager.getLogger();
    private ConcurrentMap<String, Context> contextMap = new ConcurrentHashMap<>();
    private AtomicInteger contextIdRef = new AtomicInteger(0);

    private static volatile WorkflowServiceImpl instance;
    private WorkflowServiceImpl() {}

    public static WorkflowServiceImpl getInstance() {
        if (instance == null) {
            synchronized (WorkflowServiceImpl.class) {
                if (instance == null) {
                    instance = new WorkflowServiceImpl();
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
        String contextId = flowType.concat("_").concat(String.valueOf(contextIdRef.incrementAndGet()));
        Context context = new Context(contextId, flowType, metadata);
        context.currentStateId = rootState.getId();
        context.conversation = ConversationManager.getInstance().getConversation(flowType, rootState.getId());
        contextMap.put(contextId, context);
        return contextId;
    }

    @Override
    public String startWorkflow(String flowType, String nextState, Map<String, ?> metadata)
    {
        if (flowType == null || flowType.isEmpty()) {
            logger.error("Cannot create workflow for empty flowType!");
            return null;
        }
        String contextId = flowType.concat("_").concat(String.valueOf(contextIdRef.incrementAndGet()));
        Context context = new Context(contextId, flowType, metadata);
        context.currentStateId = nextState;
        context.conversation = ConversationManager.getInstance().getConversation(flowType, nextState);
        contextMap.put(contextId, context);
        return contextId;
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
    public Pair<List<String>, String> getLexFulfillmentEntry(String flowType)
    {
        Conversation conversationForLex = ConversationManager.getInstance().getConversationForLex(flowType);

        List<String> questions = conversationForLex.getQuestions();
        String id = conversationForLex.getId();

        return Pair.of(questions, id);
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
        Map<String, ?> metadata;
        String currentStateId;
        ConcurrentMap<String, String> contextCache = new ConcurrentHashMap<>();
        Conversation conversation;

        Context(String contextId, String flowType, Map<String, ?> metadata) {
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
            return Pair.of(conversation.getSendTo(), question);
        }
    }
}
