package org.innov8.tcb.bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.exceptions.MessagesException;
import org.symphonyoss.client.exceptions.StreamsException;
import org.symphonyoss.client.exceptions.UsersClientException;
import org.symphonyoss.client.model.Chat;
import org.symphonyoss.client.services.ChatListener;
import org.symphonyoss.client.services.ChatServiceListener;
import org.symphonyoss.symphony.clients.model.SymMessage;
import org.symphonyoss.symphony.clients.model.SymUser;

import java.util.Collections;
import java.util.Set;

@Slf4j
public class SymphonyBot implements Bot, ChatListener, ChatServiceListener {

    @Autowired
    private SymphonyClient symphonyClient;

    @Override
    public String sendMessage(String who, String message) {
        Chat chat = new Chat();
        chat.setLocalUser(symphonyClient.getLocalUser());

        try {
            Set<SymUser> remoteUsers = Collections.singleton(symphonyClient.getUsersClient().getUserFromEmail(who));

            chat.setRemoteUsers(remoteUsers);
            chat.setStream(symphonyClient.getStreamsClient().getStream(remoteUsers));

            sendMessage(chat, message);

        } catch (UsersClientException e) {
            e.printStackTrace();
        } catch (StreamsException e) {
            e.printStackTrace();
        } catch (MessagesException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onChatMessage(SymMessage message) {

    }

    @Override
    public void onNewChat(Chat chat) {
        chat.addListener(this);
    }

    @Override
    public void onRemovedChat(Chat chat) {
        chat.removeListener(this);
    }

    private void sendMessage(Chat chat, String message)
            throws MessagesException {
        SymMessage messageSubmission = new SymMessage();
        messageSubmission.setMessageText(message);
        symphonyClient.getChatService().addChat(chat);
        symphonyClient.getMessageService().sendMessage(chat, messageSubmission);
    }
}
