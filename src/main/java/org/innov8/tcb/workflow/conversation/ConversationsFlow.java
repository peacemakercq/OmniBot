package org.innov8.tcb.workflow.conversation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class stores all the Conversations for a flowType.
 * Created by wangqi on 2019/9/5.
 */
@Component
@ConfigurationProperties("conversationmanager")
public class ConversationsFlow {
    /**
     * Specifies the flowType of the class.
     */
    private String flowType;

    private Map<String, ConversationImpl> conversationMap = new ConcurrentHashMap<>();

    public Conversation getConversation(String id) {

        if (id == null || id.isEmpty()) {
            return null;
        }
        return conversationMap.get(id);
    }

    public Map<String, ConversationImpl> getConversationMap() {
        return conversationMap;
    }

    public void setConversationMap(Map<String, ConversationImpl> conversationMap) {
        this.conversationMap = conversationMap;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }
}
