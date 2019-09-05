package org.innov8.tcb.config;

import authentication.ISymAuth;
import authentication.SymBotRSAAuth;
import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.innov8.tcb.bot.ChatBot;
import org.innov8.tcb.bot.SymphonyBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

@Configuration
public class SymphonyBotConfig {

    @Bean
    public SymConfig config() throws URISyntaxException {
        URL res = getClass().getClassLoader().getResource("symphony-config.json");
        File file = Paths.get(res.toURI()).toFile();
        String configPath = file.getAbsolutePath();
        SymConfig symConfig = new SymConfigLoader().loadFromFile(configPath);
        return symConfig;
    }

    @Lazy
    @Bean
    public SymBotClient symBotClient(SymConfig config) {
        ISymAuth botAuth = new SymBotRSAAuth(config);
        botAuth.authenticate();

        SymBotClient botClient = SymBotClient.initBot(config, botAuth);
        return botClient;
    }

    @Lazy
    @Bean("SymphonyBot")
    public ChatBot symphonyBot() {
        return new SymphonyBot();
    }
}
