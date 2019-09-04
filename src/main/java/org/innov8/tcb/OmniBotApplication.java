package org.innov8.tcb;

import org.innov8.tcb.core.conversation.ConversationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OmniBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniBotApplication.class, args);
	}

    @Autowired
    private ConversationManager convManager;

    @Override
    public void run(String... args) {
        System.out.println("conversation list: " + convManager.getConversationMap());
        //System.out.println("conversation list: " + convManager.getConversationList());
    }
}
