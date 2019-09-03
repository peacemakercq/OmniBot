package org.innov8.tcb;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.lexmodelbuilding.LexModelBuildingClient;
import software.amazon.awssdk.services.lexmodelbuilding.model.GetBotResponse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class OmniBotApplication implements ApplicationContextAware
{
	@Value("${aws.region}")
	private String awsRegion;

	private static ApplicationContext context;

	public static void main(String[] args) throws ExecutionException, InterruptedException, URISyntaxException
	{
		SpringApplication.run(OmniBotApplication.class, args);

		LexModelBuildingClient lexModelBuildingClient = getBean(LexModelBuildingClient.class);

		GetBotResponse getBotResponse = lexModelBuildingClient.getBot(builder -> {
			builder.name("OrderFlowers").versionOrAlias("Latest");
		});
		System.out.println(getBotResponse.toString());
	}

	@Bean
	public ProxyConfiguration getProxyConfiguration() throws URISyntaxException
	{
		URI uri = new URI("hkgproxy.nomura.com:80");
		return ProxyConfiguration.builder().endpoint(uri).build();
	}

	@Bean
	public SdkHttpClient getSdkHttpClient() throws URISyntaxException
	{
		return ApacheHttpClient.builder().proxyConfiguration(getProxyConfiguration()).build();
	}

	@Bean
	public LexModelBuildingClient getLexModelBuildingClient() throws URISyntaxException
	{
		return LexModelBuildingClient.builder().region(Region.of(awsRegion)).httpClient(getSdkHttpClient()).build();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
	{
		context = applicationContext;
	}

	public static <T> T getBean(Class<T> beanClazz)
	{
		return context.getBean(beanClazz);
	}

	public static Object getBean(String beanName) {
		return context.getBean(beanName);
	}

	public static <T> T getBean(Class<T> beanClazz, Object... args) {
		return context.getBean(beanClazz, args);
	}

	public static Object getBean(String beanName, Object... args) {
		return context.getBean(beanName, args);
	}

}
