package org.innov8.tcb.core.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This class maintains one states flow.
 * Created by wangqi on 2019/9/3.
 */
public class StatesFlow {

    private State rootState = new BotState("startBot");
    private State endState = new BotState("endBot");
    private ConcurrentMap<String, State> states = new ConcurrentHashMap<>();

    /**
     * Flow definition file
     */
    private final String stateFile;

    StatesFlow(String stateFile) {

        this.stateFile = stateFile;
    }

    State getState(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        if ("[*]".equals(id)) {
            return rootState;
        }
        return states.get(id);
    }

    void addState(String id, State state) {
        states.put(id, state);
    }

    State nextStateFromId(String nextId) {
        if ("[*]".equals(nextId)) {
            return endState;
        } else if (states.containsKey(nextId)) {
            return states.get(nextId);
        }
        return new BotState(nextId);
    }

    public State getRootState() {
        return rootState;
    }

    public State getEndState() {
        return endState;
    }

    public State getNext(String currentStateId, String option) {
        State state = getState(currentStateId);
        if (state == null) {
            return null;
        }
        if (option == null || option.isEmpty()) {
            option = "DEFAULT_OPTION";
        }
        return state.getNext(option);
    }

    public String getStateFile() {
        return stateFile;
    }

    @Override
    public String toString() {
        return String.format("filename: %s, states: %s", stateFile, states);
    }
}
