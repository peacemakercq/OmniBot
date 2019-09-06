package org.innov8.tcb;

import lombok.extern.slf4j.Slf4j;
import org.innov8.tcb.workflow2.StringExpansion;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
public class StringExpansionTest
{
    private static Map<String, Object> metadata = new HashMap<>();

    static {
        initMetadata();
    }
    private static void initMetadata() {
        metadata.put("q:Q1", "What's your name?");
        metadata.put("a:Q1", "Lily");
        metadata.put("q:Q2", "How old are you?");
        metadata.put("a:Q2", "19");
        metadata.put("d:email", "abc@tcb.com");
        metadata.put("d:email.Lily", "lily@tcb.com");
        metadata.put("a:Q1[3]", "the 3rd answer for QA");
    }

    private static final Pattern StaticPattern = Pattern.compile("\\$\\{d:\\w+.[\\w{}:]+}");
    @Test
    public void testExpansion() {

        String line = "Answer sheet from {a:Q1}: \n {q:Q1} - {a:Q1} \n {q:Q2} - {a:Q2} \n" +
                "Sending the details to ${d:email.{a:Q1}}";
        log.info("" + StaticPattern.matcher(line).find());
        log.info(StringExpansion.expandLine(line, metadata));

        line = "I would like to check '{a:Q1[3]}', '{a:Q1[0]}', could you help?";
        log.info(StringExpansion.expandLine(line, metadata));
    }
}
