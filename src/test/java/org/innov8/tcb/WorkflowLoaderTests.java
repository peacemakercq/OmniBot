package org.innov8.tcb;

import org.innov8.tcb.workflow2.WorkflowLoader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WorkflowLoader.class})
public class WorkflowLoaderTests {

    @Autowired
    private WorkflowLoader workflowLoader;

    @Test
    public void testStepsParsing() throws IOException {
        assertEquals(1, workflowLoader.getWorkflows().size());
        assertEquals(10, workflowLoader.getWorkflows().get("LicenseRenewal").getSteps().size());
    }
}
