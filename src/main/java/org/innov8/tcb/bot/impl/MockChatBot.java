package org.innov8.tcb.bot.impl;

import io.reactivex.rxjava3.core.Observable;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.innov8.tcb.bot.ChatBot;
import org.innov8.tcb.lex.LexServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class MockChatBot implements ChatBot
{
    private Map<String, String> slotAnswerPair = new LinkedHashMap<>();

    @Autowired
    private LexServiceImpl lexService;

    @Value("${lex.bot.name}")
    private String botName;

    @Value("${lex.bot.alias}")
    private String botAlias;

    @Value("${user.id}")
    private String userId;


    @Override
    public String sendMessage(String who, String message)
    {
        log.info("Sending {} to {}", who, message);
        return null;
    }

    @Override
    public void sendNotification(String who, String message)
    {
        log.info("Sending notification {} to {}", who, message);
    }

    @Override
    public Observable<Pair<String, String>> incomingMessage()
    {

        return null;
    }

    public void init()
    {
        slotAnswerPair.put("License", "MarketData-HK");
        slotAnswerPair.put("Frequency", "Daily");
        slotAnswerPair.put("OnlyPerson", "Yes");
        slotAnswerPair.put("CanShareLicense", "No");
        slotAnswerPair.put("Disruption", "Severe");
        slotAnswerPair.put("Workaround", "No");
        slotAnswerPair.put("DifferentAccess", "No");
    }

}
