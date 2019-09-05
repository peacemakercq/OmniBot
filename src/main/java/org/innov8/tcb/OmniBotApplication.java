package org.innov8.tcb;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class OmniBotApplication
{

	public static void main(String[] args)
	{
		SpringApplication.run(OmniBotApplication.class, args);
	}

}
