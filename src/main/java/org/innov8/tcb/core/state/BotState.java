package org.innov8.tcb.core.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Main class to store state information
 * Created by wangqi on 2019/9/3.
 */
public class BotState implements State {
    private String id;
    private ConcurrentMap<String, State> nextStates = new ConcurrentHashMap<>();

    public BotState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean addNext(String option, State nextState) {
        if (nextStates.containsKey(option)) {
            return false;
        }
        nextStates.put(option, nextState);
        return true;
    }

    public State getNext(String option) {
        if (nextStates.containsKey(option)) {
            return nextStates.get(option);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format(id + ", next options: %s", nextStates.keySet());
    }
}
