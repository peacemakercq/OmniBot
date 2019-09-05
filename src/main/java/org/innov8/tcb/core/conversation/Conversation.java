package org.innov8.tcb.core.conversation;

import java.util.List;

public interface Conversation {
    List<String> getQuestions();
    String getSendTo();
}
