package com.example.resilience.service;

import java.time.Duration;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;

/*
 * More details at: https://github.com/resilience4j/resilience4j#circuitbreaker
 *                  https://resilience4j.readme.io/docs/examples
 */
@Service
public class TestDecorator {

	final private TargetService targetService;

	public TestDecorator(TargetService targetService) {
		this.targetService = targetService;
	}

	private CircuitBreakerRegistry circuitBreakerRegistery() {
		CircuitBreakerConfig cbc = CircuitBreakerConfig//
				.custom()//
				.failureRateThreshold(50)//
				.waitDurationInOpenState(Duration.ofMillis(1000))//
				.ringBufferSizeInHalfOpenState(2)//
				.ringBufferSizeInClosedState(2)//
				.build();

		return CircuitBreakerRegistry.of(cbc);

	}

	public String testUsingSupplier() {

		/* Create a service broker instance for testService */
		CircuitBreaker circuitBreaker = circuitBreakerRegistery().circuitBreaker("targetService");

		/* Decorate the target service call around circuit breaker */
		Supplier<String> supplier = CircuitBreaker.decorateSupplier(circuitBreaker, targetService::callService);

		return Try.ofSupplier(supplier)//
				.recover(throwable -> "Hello from Recovery!!")//
				.get();
	}

}
