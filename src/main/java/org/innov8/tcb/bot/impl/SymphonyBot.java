package org.innov8.tcb.bot.impl;

import clients.SymBotClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import listeners.IMListener;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import model.InboundMessage;
import model.OutboundMessage;
import model.Stream;
import model.UserInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.innov8.tcb.bot.ChatBot;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SymphonyBot implements ChatBot, IMListener {

    @Autowired
    private SymBotClient symBotClient;

    private PublishSubject<Pair<String, String>> incomingMessages;

    public void init() {
        incomingMessages = PublishSubject.create();
        symBotClient.getDatafeedEventsService().addIMListener(this);
    }

    @Override
    public String sendMessage(String who, String message) {
        try {
            log.info("Sending symphony message: " + message + " to " + who);
            UserInfo userInfo = symBotClient.getUsersClient().getUserFromEmail(who, false);
            String streamId = symBotClient.getStreamsClient().getUserIMStreamId(userInfo.getId());
            OutboundMessage outboundMessage = new OutboundMessage();
            outboundMessage.setMessage(message);
            symBotClient.getMessagesClient().sendMessage(streamId, outboundMessage);

            StringBuilder sb = new StringBuilder();
            CountDownLatch cdl = new CountDownLatch(1);
            var subscription = incomingMessages.subscribe(s -> {
                sb.append(s.getValue());
                cdl.countDown();
            });
            cdl.await(30, TimeUnit.SECONDS);
            subscription.dispose();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Observable<Pair<String, String>> incomingMessage() {
        return incomingMessages;
    }

    @Override
    public void onIMMessage(InboundMessage message) {
        incomingMessages.onNext(Pair.of(
                message.getUser().getEmail(),
                message.getMessageText()));
    }

    @Override
    public void onIMCreated(Stream stream) {
    }
}
