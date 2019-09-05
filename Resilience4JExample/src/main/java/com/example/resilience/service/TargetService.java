package com.example.resilience.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TargetService {

	final private RestTemplate restTemplate;

	public TargetService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public String callService() {

		System.out.println("Actual service called");

		String message = restTemplate.getForObject("http://localhost:8080/api/message", String.class);
		return message;
	}
}
