package com.example.resilience.service;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Try;

/*
 * More details at: https://github.com/resilience4j/resilience4j#circuitbreaker
 */
@Service
public class TestService {

	final private RestTemplate restTemplate;

	public TestService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig//
				.custom()//
				.failureRateThreshold(10)// Percentage of calls fail to open a circuit
				.waitDurationInOpenState(Duration.ofSeconds(30))// How much time to keep the circuit open
				.ringBufferSizeInClosedState(10)// Track last 10 calls
				.build();
	}

	public String testUsingSupplier() {

		/* Create a service broker instance for testService */
		CircuitBreaker circuitBreaker = CircuitBreaker.of("targetService", this::circuitBreakerConfig);

		/* Decorate the target service call around circuit breaker */
		Supplier<String> supplier = CircuitBreaker.decorateSupplier(circuitBreaker, this::callService);

		return Try.ofSupplier(supplier).recover(throwable -> "Hello from Recovery").get();
	}

	public String callService() {

		System.out.println("Actual service called");

		String message = restTemplate.getForObject("http://localhost:8080/api/message", String.class);
		return message;
	}

}
