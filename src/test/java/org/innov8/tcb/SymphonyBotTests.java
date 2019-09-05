package org.innov8.tcb;

import org.innov8.tcb.bot.ChatBot;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SymphonyBotTests {

    @Autowired
    private ChatBot bot;

    @Test
    @Ignore("integration test.")
    public void testSendMessage() {
        String reply = bot.sendMessage("jbgray@btinternet.com", "i'm bot...");
        assertEquals("hi", reply);
    }

    @Test
    @Ignore("integration test.")
    public void testReceiveMessage() {
        bot.incomingMessage().subscribe(p -> {
            assertEquals("jbgray@btinternet.com", p.getKey());
            assertEquals("yeah", p.getValue());
        });
    }
}
