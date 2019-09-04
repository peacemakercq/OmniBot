package org.innov8.tcb;

import lombok.extern.log4j.Log4j2;
import org.innov8.tcb.common.utils.SpringUtil;
import org.innov8.tcb.lex.LexServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotResponse;
import software.amazon.awssdk.services.lexruntime.model.PostTextResponse;
import software.amazon.awssdk.services.lexruntime.model.PutSessionResponse;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Log4j2
public class OmniBotApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(OmniBotApplication.class, args);

		Map<String, String> slotAnswerPair = new LinkedHashMap<>();
		slotAnswerPair.put("License", "MarketData-HK");
		slotAnswerPair.put("Frequency", "Daily");
		slotAnswerPair.put("OnlyPerson", "Yes");
		slotAnswerPair.put("CanShareLicense", "No");
		slotAnswerPair.put("Disruption", "Severe");
		slotAnswerPair.put("Workaround", "No");
		slotAnswerPair.put("DifferentAccess", "No");

		final boolean finalAnswer = true;


		LexServiceImpl lexService = SpringUtil.getBean(LexServiceImpl.class);
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
