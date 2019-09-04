package org.innov8.tcb.core.conversation;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manage Conversations
 * Created by wangqi on 2019/9/4.
 */
@Component
@ConfigurationProperties("conversationmanager")
public class ConversationManager {
    private Map<String, ConversationEntity> conversationMap = new ConcurrentHashMap<>();

    public Entity getConversation(String id) {

        if (id == null || id.isEmpty()) {
            return null;
        }
        return conversationMap.get(id);
    }

    public Map<String, ConversationEntity> getConversationMap() {
        return conversationMap;
    }

    public void setConversationMap(Map<String, ConversationEntity> conversationMap) {
        this.conversationMap = conversationMap;
    }
}
