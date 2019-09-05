package org.innov8.tcb;

import org.apache.commons.lang3.tuple.Pair;
import org.innov8.tcb.workflow2.WorkflowLoader;
import org.innov8.tcb.workflow2.WorkflowService;
import org.innov8.tcb.workflow2.WorkflowServiceImpl2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {WorkflowLoader.class, WorkflowServiceImpl2.class})
public class WorkflowServiceTests {

    private static final String TEST_FLOW = "LicenseRenewal";

    @Autowired
    private WorkflowService workflowService;

    @Test
    public void initializeWorkflow_invoked_returnValidContextId() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        assertNotNull(contextId);
        assertTrue(contextId.startsWith(TEST_FLOW));
    }

    @Test
    public void nextStep_firstCalled_returnQuestion1() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        Pair nextQuestion = workflowService.nextStep(contextId, null);
        assertEquals("Do you want to renew {d:license}?\\n{d:pre_defined_info}", nextQuestion.getLeft());
        assertEquals("appOwner", nextQuestion.getLeft());
    }

    @Test
    public void nextStep_wantToRenew_gotoQuestionnaire() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        assertEquals("How often do you use the application that you wish to renew license for?", nextQuestion.getLeft());
        assertEquals("appOwner", nextQuestion.getLeft());
    }

    @Test
    public void nextStep_dontWantToRenew_exit() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "No");
        assertNull(nextQuestion);
    }

    @Test
    public void nextStep_scenario3_goGetManagerApproval() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes"); // Q3
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No"); // Q6

        assertEquals("Please approve {d:user}'s request to renew {d:license}\\n{q:questionnaire[0]}-{a:questionaire[0]}", nextQuestion.getLeft());
        assertEquals("manager", nextQuestion.getLeft());
    }

    @Test
    public void nextStep_scenario3AnotherCase_goGetManagerApproval() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);
        // Except Q3 and Q6, other answers are not in the critical paths
        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "Yes"); // Q3
        nextQuestion = workflowService.nextStep(contextId, "IDK");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No"); // Q6

        assertEquals("Please approve {d:user}'s request to renew {d:license}\\n{q:questionnaire[0]}-{a:questionaire[0]}", nextQuestion.getLeft());
        assertEquals("manager", nextQuestion.getLeft());
    }

    @Test
    public void nextStep_scenario5_promptMessage() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);

        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "IDK"); // Q3
        nextQuestion = workflowService.nextStep(contextId, "Yes"); // Q4
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No"); // Q6

        assertEquals("You can consider utilizing a shared account to source the info.\n Do you still wish to proceed with this license renewal request?", nextQuestion.getLeft());
        assertEquals("appOwner", nextQuestion.getLeft());
    }

    @Test
    public void nextStep_managerApproved_goGetBMOApproval() {
        String contextId = workflowService.initializeWorkflow(TEST_FLOW, null);

        Pair nextQuestion = workflowService.nextStep(contextId, null);
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No");
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "IDK"); // Q3
        nextQuestion = workflowService.nextStep(contextId, "Yes"); // Q4
        nextQuestion = workflowService.nextStep(contextId, "Yes");
        nextQuestion = workflowService.nextStep(contextId, "No"); // Q6

        nextQuestion = workflowService.nextStep(contextId, "Yes");  // manager approved

        assertEquals("Please review license renewal request {d:pre_defined_info}", nextQuestion.getLeft());
        assertEquals("BMO", nextQuestion.getLeft());
    }
}
