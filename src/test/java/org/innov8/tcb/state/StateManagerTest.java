package org.innov8.tcb.state;

import org.junit.Test;

/**
 * test class for StateManager
 * Created by wangqi on 2019/9/3.
 */
public class StateManagerTest {
    @Test
    public void test() {
        StateManager.getInstance();
    }

    @Test
    public void testGetNext() {
        state.State nextState = StateManager.getInstance().getNext("RenewStart", "Yes");
        System.out.println("nextState is: " + nextState);
    }
}
