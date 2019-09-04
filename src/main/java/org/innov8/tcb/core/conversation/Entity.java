package org.innov8.tcb.core.conversation;

import javafx.util.Pair;

import java.util.List;

public interface Entity {
    List<String> getConversations();
    String getSendTo();
    Pair<String, String> getNext();
}
