package org.innov8.tcb.workflow2;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Service
public class WorkflowServiceImpl2 implements WorkflowService
{

    private ConcurrentMap<String, WorkflowContext> contextMap = new ConcurrentHashMap<>();

    @Autowired
    private WorkflowLoader workflowLoader;

    @Override
    public String initializeWorkflow(@NotNull String flowName, Map<String, ?> metadata,
                                     boolean startFromLex) {
        log.info("initialize " + flowName + ", startFromLex=" + startFromLex + ", metadata=" + (metadata != null ? metadata.toString() : ""));
        Workflow workflow = workflowLoader.getWorkflows().get(flowName);
        Step currentStep = workflow.getLexStep();
        WorkflowContext workflowContext = new WorkflowContext(flowName, metadata, startFromLex ?
                currentStep : null);
        String contextId = workflowContext.getContextId();
        contextMap.put(contextId, workflowContext);
        return contextId;
    }

    @Override
    public String initializeWorkflow(String flowName, Map<String, ?> metadata)
    {
        return initializeWorkflow(flowName, metadata, false);
    }

    @Override
    public Pair<String, String> nextStep(String contextId, String condition) {
        WorkflowContext workflowContext = contextMap.get(contextId);
        log.info("Context " + contextId + " is trying yo move from " +
                (workflowContext != null && workflowContext.currentStep != null ? workflowContext.currentStep.getName() : "") +
                " to next state with: " + condition);
        Step currentStep = workflowContext.getCurrentStep();
        if (currentStep == null) {
            currentStep = getWorkflow(workflowContext).getEntranceStep();
            workflowContext.setCurrentStep(currentStep);
        } else {
            // store previous question and answer into metadata
            saveAnswer(workflowContext, condition);
        }

        if (workflowContext.currentQuestionIndex >= 0) {
            workflowContext.currentConditions[workflowContext.currentQuestionIndex] = condition;
        }

        int nextQuestionIndex = ++workflowContext.currentQuestionIndex;

        // if presumed next question index is greater than questions size - 1
        // means we're out of questions, several things to do

        List<String> questions = currentStep.getQuestions();
        if (nextQuestionIndex > questions.size() - 1) {
            // 1. reset question index to 0 for the next step
            workflowContext.currentQuestionIndex = -1;

            // 2. judge if the input condition matches the condition of the current step
            List<NextStep> nextSteps = currentStep.getNextSteps();

            for (NextStep nextStep : nextSteps) {
                if (matches(workflowContext.currentConditions, nextStep.getCondition())) {
                    String name = nextStep.getName();
                    Step forwardingStep = getWorkflow(workflowContext).getSteps().get(name);
                    if (forwardingStep != null) {
                        workflowContext.setCurrentStep(forwardingStep);
                        String questionTemplate =
                                forwardingStep.getQuestions().get(++workflowContext.currentQuestionIndex);

                        //TODO populate the placeholder
                        String concreteQuestion = populateQuestion(questionTemplate, workflowContext);

                        saveQuestion(workflowContext, concreteQuestion);
                        log.info("Context " + contextId + " is sending next question: " + concreteQuestion + " to " + forwardingStep.getSendTo());
                        return Pair.of(concreteQuestion, forwardingStep.getSendTo());
                    }
                }
            }

        } else {
            // 1. collect current condition
            // 2. get next question and sendTo, return Pair<question,recipient>
            String recipient = currentStep.getSendTo();
            String questionTemplate = questions.get(nextQuestionIndex);
            String concreteQuestion = populateQuestion(questionTemplate, workflowContext);
            saveQuestion(workflowContext, concreteQuestion);
            return Pair.of(concreteQuestion, recipient);
        }
        // could be
        // can't find next step
        // can't parse answer
        // finish the whole flow
        return null;
    }

    private void saveQuestion(WorkflowContext workflowContext, String concreateQuestion)
    {

    }

    private String populateQuestion(String questionTemplate, WorkflowContext context)
    {
        return questionTemplate;
    }

    private void saveAnswer(WorkflowContext workflowContext, String condition)
    {

    }

    private boolean matches(String[] actualConditions, String configuredConfigStr)
    {
        String[] configuredConditions = configuredConfigStr.split("\\|");
        if (actualConditions.length != configuredConditions.length)
        {
            log.error("Presumed conditions length does not match collected conditions");
            throw new IllegalArgumentException("Please check your configured condition count!");
        }

        for (int i = 0; i<configuredConditions.length; i++)
        {
            if (!configuredConditions[i].equalsIgnoreCase(actualConditions[i])
            && !configuredConditions[i].equalsIgnoreCase("any")) return false;
        }

        return true;
    }

    @Override
    public Pair<List<String>, String> getLexFulfillmentEntry(String flowName)
    {
        return null;
    }

    @Getter
    private static final class WorkflowContext {
        private String contextId;
        private String flowName;
        private Map<String, ?> metadata = new HashMap<>();
        private Step currentStep;
        private int currentQuestionIndex = -1;

        /**
         * current condition for the current step
         */
        private String[] currentConditions;

        private WorkflowContext(String typeName, Map<String, ?> metadata,
                                Step currentStep) {
            this.contextId = typeName + UUID.randomUUID().toString();
            this.flowName = typeName;
            if (metadata != null) {
                this.metadata = metadata;
            }
            setCurrentStep(currentStep);
        }

        private void setCurrentStep(Step step) {
            if(step!=null) {
                this.currentStep = step;
                this.currentConditions = new String[currentStep.getQuestions().size()];
            }
        }
    }

    private Workflow getWorkflow(WorkflowContext context) {
        return workflowLoader.getWorkflows().get(context.flowName);
    }
}
