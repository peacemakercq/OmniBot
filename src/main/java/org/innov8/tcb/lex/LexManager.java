package org.innov8.tcb.lex;


import io.reactivex.rxjava3.disposables.Disposable;
import lombok.extern.log4j.Log4j2;
import org.innov8.tcb.bot.ChatBot;
import org.innov8.tcb.workflow2.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotResponse;
import software.amazon.awssdk.services.lexruntime.model.DialogState;
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse;
import software.amazon.awssdk.services.lexruntime.model.PutSessionResponse;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

@Log4j2
public class LexManager
{
    private Map<Integer, String> intentMap = new HashMap<>();

    @Resource(name = "ChatBotExecutor")
    private ScheduledExecutorService scheduledExecutorService;

    @Resource(name = "SymphonyBot")
    private ChatBot chatBot;

    @Autowired
    private LexServiceImpl lexService;

    @Autowired
    private WorkflowService workflow;

    private Disposable subscription;

    public void init() {

        intentMap.put(1, "Frequency");
        intentMap.put(2, "OnlyPerson");
        intentMap.put(3, "CanShareLicense");
        intentMap.put(4, "Disruption");
        intentMap.put(5, "Workaround");
        intentMap.put(6, "DifferentAccess");

        subscription = chatBot.incomingMessage().subscribe(pair -> {

            String recipient = pair.getKey();
            String incomingMessage = pair.getValue();

            PostTextResponse postTextResponse = lexService.postText(incomingMessage);
            String flowType = postTextResponse.intentName();
            DialogState dialogState = postTextResponse.dialogState();

            if (dialogState != DialogState.READY_FOR_FULFILLMENT)
            {
                chatBot.sendMessage(recipient, postTextResponse.message());
            }
            else
            {
                chatBot.sendMessage(recipient, "Thank you for your support, have a nice day, Bye.");
                String contextId = workflow.initializeWorkflow(flowType, null, true);
                Map<String, String> slots = postTextResponse.slots();
                for (int i = 0 ; i< intentMap.size(); i++)
                {
                    String intent = intentMap.get(i+1);
                    String answer = slots.get(intent);
                    workflow.nextStep(contextId, answer);
                }

            }
        });
    }

    @PreDestroy
    public void destroyDisposable()
    {
        subscription.dispose();
    }

    public void testStartRenewal()
    {
        Map<String, String> slotAnswerPair = new LinkedHashMap<>();
        slotAnswerPair.put("License", "MarketData-HK");
        slotAnswerPair.put("Frequency", "Daily");
        slotAnswerPair.put("OnlyPerson", "Yes");
        slotAnswerPair.put("CanShareLicense", "No");
        slotAnswerPair.put("Disruption", "Severe");
        slotAnswerPair.put("Workaround", "No");
        slotAnswerPair.put("DifferentAccess", "No");

        final boolean finalAnswer = true;

        GetBotResponse getResponse = lexService.getBotInfo("StartRenewalBot", "DEV");
        log.info(getResponse.toString());


        log.info("Kick off Lex bot ........... begin");
        PutSessionResponse putResponse = lexService.putSession("License");
        log.info("Lex Bot response:{}", putResponse.message());
        String slotToElicit = putResponse.slotToElicit();

        Iterator<Map.Entry<String, String>> it = slotAnswerPair.entrySet().iterator();
        PostTextResponse postTextResponse = null;
        while (it.hasNext())
        {
            Map.Entry<String, String> next = it.next();
            String slot = next.getKey();
            String answer = next.getValue();

            if (slot.equals(slotToElicit))
            {
                postTextResponse = lexService.postText(answer);
                log.info("Lex Bot Response: {}", postTextResponse.message());
                slotToElicit = postTextResponse.slotToElicit();
                it.remove();
            }
        }
        if (postTextResponse != null && postTextResponse.dialogState().toString().equals(
                "ConfirmIntent"))
        {
            PostTextResponse finalResultResponse = lexService.postText(finalAnswer?"YES":"No");
            log.info("Lex Bot conversation finished with result: {}", finalResultResponse.slots());
        }
    }

}
