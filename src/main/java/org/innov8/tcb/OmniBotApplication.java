package org.innov8.tcb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OmniBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(OmniBotApplication.class, args);
	}
}
