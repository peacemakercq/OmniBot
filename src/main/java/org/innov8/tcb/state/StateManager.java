package org.innov8.tcb.state;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

/**
 * Manage the states
 * Created by wangqi on 2019/9/3.
 */
public class StateManager {
    private volatile static StateManager instance;
    private final String DEFAULT_OPTION = "DEFAULT_OPTION";
    private State rootState = new BotState("startBot");
    private State endState = new BotState("endBot");
    private ConcurrentMap<String, State> states = new ConcurrentHashMap<>();

    private Pattern pattern = Pattern.compile(" *(\\w+|\\[\\*]) *--> *(\\w+|\\[\\*]) *(:.*|)");
    private StateManager() {


        parseLine("[*] --> RenewStart");
        parseLine("RenewStart --> doRenew : Yes");
        parseLine("RenewStart --> noRenew : No");
        parseLine("RenewStart --> renewFAQ : Others");
        parseLine("renewFAQ --> RenewStart");
        parseLine("doRenew --> [*]");
        parseLine("noRenew --> [*]");

        System.out.println("States: " + states);
    }

    public static StateManager getInstance() {
        if (instance == null) {
            synchronized (StateManager.class) {
                if (instance == null) {
                    instance = new StateManager();
                }
            }
        }
        return instance;
    }

    /**
     * Create state from line.
     * @param line read line from file
     */
    public void parseLine(String line) {
        if (!pattern.matcher(line).matches()) {
            return;
        }
        String[] split = line.split("-->");
        String id = split[0].trim();
        String nextInfo = split[1];
        int index = nextInfo.indexOf(':');


        System.out.println("line: " + line + ", id: " + id + ", nextInfo: " + nextInfo);

        if (index > 0) {

        }
        String nextId = (index > 0) ? nextInfo.substring(0, index).trim() : nextInfo.trim();
        String option = DEFAULT_OPTION;
        if (index > 0 && nextInfo.length() >= index) {
            option = nextInfo.substring(index + 1).trim();
        }
        State nextState = nextStateFromId(nextId);
        states.put(nextId, nextState);
        State state = getState(id);
        if (state == null) {
            System.out.println("Error! state is not definded yet! id={}" + id);
            return;
        }
        state.addNext(option, nextState);


    }

    private State getState(String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        if ("[*]".equals(id)) {
            return rootState;
        }
        return states.get(id);
    }

    private State nextStateFromId(String nextId) {
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
            option = DEFAULT_OPTION;
        }
        return state.getNext(option);
    }
}
