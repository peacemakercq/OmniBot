package org.innov8.tcb.core.conversation;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConversationEntity implements Conversation {
    private String id;
    private String sendTo;
    private List<String> questions = new ArrayList<>();

    public String getSendTo() {
        return sendTo;
    }

    public void setSendTo(String sendTo) {
        this.sendTo = sendTo;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String toString() {
        return "\nid: " + id + ", sendTo: " + sendTo + ", Conversation: " + questions;
    }

}
