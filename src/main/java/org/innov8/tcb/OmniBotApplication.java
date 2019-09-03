package org.innov8.tcb;

import lombok.extern.log4j.Log4j2;
import org.innov8.tcb.common.utils.SpringUtil;
import org.innov8.tcb.lex.LexServiceImpl;
import org.innov8.tcb.lex.entity.BotEntity;
import org.springframework.boot.SpringApplication;

@Log4j2
public class OmniBotApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(OmniBotApplication.class, args);

		LexServiceImpl lexService = SpringUtil.getBean(LexServiceImpl.class);
		BotEntity botInfo = lexService.getBotInfo("OrderFlowers", "Dev");
		lexService.CreateBot();
		log.info(botInfo);
	}

}
