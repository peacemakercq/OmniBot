package org.innov8.tcb.config;

import org.innov8.tcb.bot.SymphonyBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.symphonyoss.client.SymphonyClient;
import org.symphonyoss.client.SymphonyClientConfig;
import org.symphonyoss.client.SymphonyClientFactory;
import org.symphonyoss.client.exceptions.AuthenticationException;
import org.symphonyoss.client.exceptions.InitException;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Configuration
public class SymphonyConfig {

   @Bean
   private SymphonyClientConfig symphonyClientConfig() throws URISyntaxException {
      URL resource = getClass().getClassLoader().getResource("symphony.properties");
      File configFile = Paths.get(resource.toURI()).toFile();

      return new SymphonyClientConfig(configFile.getAbsolutePath());
   }

   @Bean
   SymphonyClient symphonyClient(SymphonyClientConfig config) throws InitException, AuthenticationException {
      SymphonyClient symphonyClient = SymphonyClientFactory.getClient(SymphonyClientFactory.TYPE.BASIC);
      symphonyClient.init(config);
      return symphonyClient;
   }


   @Bean
   private SymphonyBot symphonyBot() {
      return new SymphonyBot();
   }
}
