package com.example.resilience.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class Config {

	@Bean
	public RestTemplate restTemplate() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
		clientHttpRequestFactory.setConnectTimeout(3000);
		clientHttpRequestFactory.setReadTimeout(3000);
		clientHttpRequestFactory.setConnectionRequestTimeout(3000);
		
		return new RestTemplate(clientHttpRequestFactory);
	}
}
