package org.innov8.tcb.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.innov8.tcb.core.state.State;
import org.innov8.tcb.core.state.StateManager;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * test class for StateManager
 * Created by wangqi on 2019/9/3.
 */
public class StateManagerTest {
    private Logger logger = LogManager.getLogger();
    private StateManager instance = StateManager.getInstance();
    private String filename = "umlState.puml";

    @Test
    public void testGetNext() {
        State rootState = instance.getRootState(filename);
        State nextState = instance.getNext(filename, rootState.getId(), null);
        logger.info("Next state for [{},{}] is {}", rootState.getId(), null, nextState);

        // todo add more test cases
        assertNext("renewFAQ", "xxx", "RenewStart");
        assertNext("doRenew", "dx", "ManagerFAQ");
    }
    private void assertNext(String id, String option, String expectation) {
        State nextState = instance.getNext(filename, id, option);
        logger.info("Next state for [{},{}] is {}. Expect: {}", id, option, nextState, expectation);
        if (nextState == null && expectation == null) {
            return;
        }
        assert nextState != null;
        assertEquals(expectation, nextState.getId());
    }
}
