package org.innov8.tcb.config;


import org.innov8.tcb.lex.LexManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexruntime.LexRuntimeClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
@EnableAutoConfiguration
public class BotBeanConfiguration
{
    @Value("${aws.region}")
    private String awsRegion;

    @Value("${chatbot.executor.coresize}")
    private int coreSize;

    @Bean
    public ProxyConfiguration getProxyConfiguration()
    {
        return ProxyConfiguration.builder().build();
    }

    @Bean
    public SdkHttpClient getSdkHttpClient()
    {
        return ApacheHttpClient.builder().proxyConfiguration(getProxyConfiguration()).build();
    }

    @Bean
    public LexModelBuildingClient getLexModelBuildingClient()
    {
        return LexModelBuildingClient.builder().region(Region.of(awsRegion))
                .httpClient(getSdkHttpClient()).build();
    }

    @Bean
    public LexRuntimeClient getLexRuntimeClient()
    {
        return LexRuntimeClient.builder().region(Region.of(awsRegion))
                .httpClient(getSdkHttpClient()).build();
    }

    @Bean(initMethod = "init")
    public LexManager getLexManager()
    {
        return new LexManager();
    }

    @Bean("ChatBotExecutor")
    public ScheduledExecutorService chatBotExecutor()
    {
        return Executors.newScheduledThreadPool(coreSize);
    }

}
