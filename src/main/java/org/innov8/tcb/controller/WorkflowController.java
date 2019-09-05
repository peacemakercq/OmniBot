package org.innov8.tcb.controller;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.innov8.tcb.bot.ChatBot;
import org.innov8.tcb.workflow2.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Slf4j
public class WorkflowController {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private ChatBot chatBot;

    @PostMapping(
            path="/{workflow}",
            consumes="application/json",
            produces="application/json")
    @ResponseBody
    public String startWorkflow(
            @PathVariable("workflow") String workflowName,
            @RequestBody Map<String, Object> metadata) {
        log.info("Received request to run workflow " + workflowName + ", metadata: " +  metadata.toString());
        var contextId = workflowService.initializeWorkflow(workflowName, metadata, false);
        var nextConversation = workflowService.nextStep(contextId, null);
        while (nextConversation != null) {
            var userAnswer = chatBot.sendMessage(nextConversation.getRight(), nextConversation.getLeft());
            nextConversation = workflowService.nextStep(contextId, userAnswer);
        }
        return contextId;
    }
}
