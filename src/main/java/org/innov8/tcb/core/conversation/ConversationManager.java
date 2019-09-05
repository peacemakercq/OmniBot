package org.innov8.tcb.core.conversation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage Conversations
 * Created by wangqi on 2019/9/4.
 */
public class ConversationManager {

    private Logger logger = LogManager.getLogger();
    private Map<String, ConversationsFlow> conversationsFlowMap = new ConcurrentHashMap<>();

    private static volatile ConversationManager instance;

    @Autowired
    private ConversationsFlow convFlow;
    private ConversationManager() {
        //conversationsFlowMap.put(convFlow.getFlowType(), convFlow);
    }

    public static ConversationManager getInstance() {
        if (instance == null) {
            synchronized (ConversationManager.class) {
                if (instance == null) {
                    instance = new ConversationManager();
                }
            }
        }
        return instance;
    }
    public void addConversationsFlow(String flowType, ConversationsFlow flow) {
        if (conversationsFlowMap.containsKey(flowType)) {
            logger.warn("The flow already exist for flowType: {}. Override!", flowType);
        }
        conversationsFlowMap.put(flowType, flow);
    }
    public ConversationsFlow getConversationsFlow(String flowType) {

        if (flowType == null || flowType.isEmpty()) {
            return null;
        }
        return conversationsFlowMap.get(flowType);
    }

    public Map<String, ConversationsFlow> getConversationsFlowMap() {
        return conversationsFlowMap;
    }

    public void setConversationsFlowMap(Map<String, ConversationsFlow> conversationsFlowMap) {
        this.conversationsFlowMap = conversationsFlowMap;
    }

    public Conversation getConversation(String flowType, String id) {
        assert flowType != null;
        ConversationsFlow conversationsFlow = getConversationsFlow(flowType);
        if (conversationsFlow == null) {
            logger.warn("ConversationsFlow is not defined for flowType: {}", flowType);
            return null;
        }
        Conversation conversation = conversationsFlow.getConversation(id);
        return conversation;
    }
}
