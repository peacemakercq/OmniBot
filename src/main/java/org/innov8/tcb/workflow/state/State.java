package org.innov8.tcb.workflow.state;

/**
 * Interface for State
 * Created by wangqi on 2019/9/3.
 */
public interface State {

    String getId();
    boolean addNext(String option, State nextState);
    State getNext(String option);
    boolean containOption(String option);
}
