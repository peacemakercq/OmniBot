package org.innov8.tcb.workflow.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Main class to store state information
 * Created by wangqi on 2019/9/3.
 */
public class BotState implements State {
    private Logger logger = LogManager.getLogger();

    private String id;
    private ConcurrentMap<String, State> nextStates = new ConcurrentHashMap<>();

    BotState(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public boolean addNext(String option, State nextState) {
        if (nextStates.containsKey(option)) {
            logger.warn("option {} exist in {} already! not add <{},{}>", option, id, option, nextState);
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

    public boolean containOption(String option) {
        if (option == null || option.isEmpty()) {
            return false;
        }
        return nextStates.keySet().contains(option);
    }

    @Override
    public String toString() {
        return String.format("{%s, next options: %s)", id, nextStates.keySet());
    }
}
