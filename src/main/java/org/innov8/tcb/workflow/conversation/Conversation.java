package org.innov8.tcb.workflow.conversation;

import java.util.List;

public interface Conversation {
    List<String> getQuestions();
    String getSendTo();
    String getId();
}
