package org.innov8.tcb.bot;

import clients.SymBotClient;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.util.Pair;
import listeners.IMListener;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import model.InboundMessage;
import model.OutboundMessage;
import model.Stream;
import model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SymphonyBot implements ChatBot, IMListener {

    @Autowired
    private SymBotClient symBotClient;

    private PublishSubject<Pair<String, String>> incomingMessages;

    @PostConstruct
    public void init() {
        incomingMessages = PublishSubject.create();
        symBotClient.getDatafeedEventsService().addIMListener(this);
    }

    @Override
    public String sendMessage(String who, String message) {
        try {
            UserInfo userInfo = symBotClient.getUsersClient().getUserFromEmail(who, false);
            String streamId = symBotClient.getStreamsClient().getUserIMStreamId(userInfo.getId());
            OutboundMessage outboundMessage = new OutboundMessage();
            outboundMessage.setMessage(message);
            symBotClient.getMessagesClient().sendMessage(streamId, outboundMessage);

            StringBuilder sb = new StringBuilder();
            CountDownLatch cdl = new CountDownLatch(1);
            var subscription = incomingMessages.subscribe(s -> {
                sb.append(s);
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
        incomingMessages.onNext(new Pair<>(
                message.getUser().getEmail(),
                message.getMessageText()));
    }

    @Override
    public void onIMCreated(Stream stream) {
    }
}
