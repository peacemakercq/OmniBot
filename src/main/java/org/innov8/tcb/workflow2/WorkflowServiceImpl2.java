package org.innov8.tcb.workflow2;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public class WorkflowServiceImpl2 implements WorkflowService
{

    private ConcurrentMap<String, WorkflowContext> contextMap = new ConcurrentHashMap<>();

    @Autowired
    private WorkflowLoader workflowLoader;

    @Override
    public String initializeWorkflow(@NotNull String flowName, Map<String, ?> metadata,
                                     boolean startFromLex)
    {
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
    public Pair<String, String> nextStep(String contextId, String condition)
    {
        WorkflowContext workflowContext = contextMap.get(contextId);
        Step currentStep = workflowContext.getCurrentStep();
        assert currentStep!=null;

        List<String> questions = currentStep.getQuestions();

        // store previous question and answer into metadata
        saveAnswer(workflowContext, condition);



        int nextQuestionIndex = workflowContext.currentQuestionIndex.incrementAndGet();

        // if presumed next question index is greater than questions size - 1
        // means we're out of questions, several things to do

        if (nextQuestionIndex > questions.size() - 1)
        {
            // 1. reset question index to 0 for the next step
            workflowContext.currentQuestionIndex.set(-1);

            // 2. judge if the input condition matches the condition of the current step
            List<NextStep> nextSteps = currentStep.getNextSteps();

            for(NextStep nextStep : nextSteps)
            {
                if (matches(workflowContext.currentConditions, nextStep.getCondition()))
                {
                    String name = nextStep.getName();
                    Workflow workflow = workflowLoader.getWorkflows().get(workflowContext.flowName);
                    Step forwardingStep = workflow.getSteps().get(name);
                    workflowContext.currentStep = forwardingStep;
                    String questionTemplate =
                            forwardingStep.getQuestions().get(workflowContext.currentQuestionIndex.incrementAndGet());

                    //TODO populate the placeholder
                    String concreteQuestion = populateQuestion(questionTemplate, workflowContext);

                    saveQuestion(workflowContext, concreteQuestion);
                    //TODO clear // 3. reset conditions to empty
                    return Pair.of(concreteQuestion, forwardingStep.getSendTo());
                }
            }

        }
        else
        {
            // 1. collect current condition
            // 2. get next question and sendTo, return Pair<question,recipient>
            workflowContext.currentConditions[nextQuestionIndex] = condition;
            String recipient = currentStep.getSendTo();
            String questionTemplate = questions.get(nextQuestionIndex);
            String concreteQuestion = populateQuestion(questionTemplate, workflowContext);
            saveQuestion(workflowContext, concreteQuestion);
            return Pair.of(concreteQuestion, recipient);
        }
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

    private boolean matches(String[] currentConditions, String conditionStr)
    {
        String[] conditions = conditionStr.split("\\|");
        if (currentConditions.length != conditions.length)
        {
            log.error("Presumed conditions length does not match collected conditions");
            return false;
        }

        for (int i = 0; i<conditions.length; i++)
        {
            if (!conditions[i].equalsIgnoreCase(currentConditions[i])) return false;
        }

        return true;
    }

    @Override
    public Pair<List<String>, String> getLexFulfillmentEntry(String flowName)
    {
        return null;
    }

    @Getter
    private static final class WorkflowContext
    {
        private String contextId;
        private String flowName;
        private Map<String, ?> metadata;
        private Step currentStep;
        private AtomicInteger currentQuestionIndex = new AtomicInteger(-1);

        /**
         * current condition for the current step
         */
        private String[] currentConditions;

        private WorkflowContext(String typeName, Map<String, ?> metadata,
                                Step currentStep) {
            this.contextId = typeName + UUID.randomUUID().toString();
            this.flowName = typeName;
            this.metadata = metadata;
            this.currentStep = currentStep;
            this.currentConditions = currentStep == null ? null :
                    new String[currentStep.getQuestions().size()];
        }

    }
}
