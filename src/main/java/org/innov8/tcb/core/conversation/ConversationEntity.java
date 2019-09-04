package org.innov8.tcb.core.conversation;

import javafx.util.Pair;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConversationEntity implements Entity{
    private String id;
    private String sendTo;
    private List<String> conversations = new ArrayList<>();

    public String getSendTo() {
        return sendTo;
    }

    private int index = 0;
    @Override
    public Pair<String, String> getNext() {
        if (conversations.size() >= index) {
            index = 0;
            return null;
        }
        String question = conversations.get(index++);
        return new Pair<>(sendTo, question);
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public List<String> getConversations() {
        return conversations;
    }

    public void setConversations(List<String> conversations) {
        this.conversations = conversations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return "\nid: " + id + ", sendTo: " + sendTo + ", Conversation: " + conversations;
    }
}
