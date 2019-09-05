package org.innov8.tcb.bot;

import io.reactivex.rxjava3.core.Observable;

public interface ChatBot {
    /**
     * Sends a message to some user and replies whatever user responses.
     *
     * @param who     the user to send the message to
     * @param message the message to send
     * @return whatever user responses
     */
    String sendMessage(String who, String message);

    /**
     * Gets the incoming message Observable.
     *
     * @return the incoming message Observable
     */
    Observable<String> incomingMessage();
}
