package org.innov8.tcb.lex;


import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import lombok.extern.log4j.Log4j2;
import org.innov8.tcb.lex.entity.BotEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.*;
import software.amazon.awssdk.services.lexruntime.LexRuntimeClient;
import software.amazon.awssdk.services.lexruntime.model.PostTextRequest;
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse;
import software.amazon.awssdk.services.lexruntime.model.PutSessionResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class LexServiceImpl
{

    @Autowired
    private LexModelBuildingClient lexModelBuildingClient;


    @Autowired
    private LexRuntimeClient lexRuntimeClient;


    public GetBotResponse getBotInfo(@NotNull final String botName, @NotNull final String botAlias)
    {
        return lexModelBuildingClient
                .getBot(builder -> builder.name(botName).versionOrAlias(botAlias));

    }


    public BotEntity CreateBot()
    {
        Map<String, List<EnumerationValue>> slotTypeMap = new HashMap<>();
        List<EnumerationValue> values = Lists.newArrayList();
        values.add(EnumerationValue.builder().value("MarketData-HK").build());
        slotTypeMap.put("License", values);

        List<Slot> slots = putSlotTypes(slotTypeMap);
        Intent intent = putIntents("StartRenewal", slots);

        PutBotResponse putBotResponse = lexModelBuildingClient.putBot(builder -> builder
                .childDirected(false)
                .name("StartRenewalBot")
                .abortStatement(getAbortStatement("Sorry, I can't help you this time"))
                .clarificationPrompt(getClarificationPrompt("I didn't understand you, what would you like to do?"))
                .description("Start license renewal process")
                .locale(Locale.EN_US)
                .processBehavior("SAVE")
                .intents(intent).build());

        return BotEntity.fromPutResponse(putBotResponse);
    }


    public void postText()
    {
    }

    public PutSessionResponse putSession(String slotToElicit)
    {
        ResponseInputStream<PutSessionResponse> inputStream = lexRuntimeClient.putSession(builder -> builder
                .accept("text/plain; charset=utf-8")
                .botAlias("DEV")

                .botName("StartRenewalBot")
                .userId("OminiBot")
                .dialogAction(dialogActionBuilder -> dialogActionBuilder
                        .intentName("StartRenewal")
                        .messageFormat("PlainText")
                        .slotToElicit(slotToElicit)
                        .type("Delegate")
                        .build())
                .build());
        return inputStream.response();
    }


    public PostTextResponse postText(String answer)
    {
        PostTextRequest postTextRequest = PostTextRequest.builder().botAlias("DEV")
                .botName("StartRenewalBot")
                .inputText(answer)
                .userId("OminiBot").build();

        log.info("Post text to Lex bot: {}", postTextRequest.inputText());
        return lexRuntimeClient.postText(postTextRequest);
    }


    private List<Slot> putSlotTypes(Map<String, List<EnumerationValue>> slotTypeMap)
    {
        List<Slot> slots = Lists.newArrayList();
        slotTypeMap.forEach((slotTypeName, values) -> {
            PutSlotTypeResponse putSlotTypeResponse = lexModelBuildingClient.putSlotType(builder -> builder
                    .name(slotTypeName)
                    .enumerationValues(values)
                    .createVersion(true)
                    .valueSelectionStrategy(SlotValueSelectionStrategy.TOP_RESOLUTION)
                    .build());
            slots.add(Slot.builder().name(putSlotTypeResponse.name())
                              .slotType(putSlotTypeResponse.name())
                              .slotTypeVersion(putSlotTypeResponse.version())
                              .slotConstraint("Required")
                              .build());
        });

        return slots;
    }

    private Intent putIntents(String intentName, List<Slot> slots)
    {
        PutIntentResponse putIntentResponse = lexModelBuildingClient.putIntent(builder -> builder
                .name(intentName)
                .conclusionStatement(getConclusionStatement("Thank you, we'll proceed the renew " +
                                                                    "process"))
                .confirmationPrompt(getConfirmationPrompt("Your {License} license will be " +
                                                                  "now going to approval process." +
                                                                  "  Does this sound okay?"))
                .fulfillmentActivity(fulfillmentActivityBuilder ->
                                             fulfillmentActivityBuilder.type(FulfillmentActivityType.RETURN_INTENT)
                                    )
                .rejectionStatement(getRejectionStatement("Okay, I will not renew your license."))
                .sampleUtterances("kick off license {License} renewal")
                .slots(slots)
                .createVersion(true)
                .build());
        return Intent.builder().intentName(intentName).intentVersion(putIntentResponse.version()).build();
    }

    private Prompt getConfirmationPrompt(String prompt)
    {
        Message message = Message.builder().content(prompt).contentType("PlainText").build();
        return Prompt.builder().maxAttempts(2).messages(message).build();

    }

    private Statement getConclusionStatement(String content)
    {
        Message message = Message.builder().content(content).contentType("PlainText").build();
        return Statement.builder().messages(message).build();
    }

    private Statement getAbortStatement(String content)
    {
        Message message = Message.builder().content(content).contentType("PlainText").build();
        return Statement.builder().messages(message).build();
    }

    private Statement getRejectionStatement(String content)
    {
        Message message = Message.builder().content(content).contentType("PlainText").build();
        return Statement.builder().messages(message).build();
    }

    private Prompt getClarificationPrompt(String prompt)
    {
        Message message = Message.builder().content(prompt).contentType("PlainText").build();
        return Prompt.builder().maxAttempts(2).messages(message).build();
    }


}
