package com.example.resilience.service;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.vavr.control.Try;

@Service
public class TestService {

	final private RestTemplate restTemplate;

	public TestService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig//
				.custom()//
				.failureRateThreshold(20)// in percentage
				.waitDurationInOpenState(Duration.ofMillis(10000))// how much time to keep the circuit open
				.ringBufferSizeInHalfOpenState(
						5)/*- allow 5 calls to go through in half open state to check if service is up again*/
				.ringBufferSizeInClosedState(10)// track last 10 calls
				.build();
	}

	public String testUsingSupplier() {

		/* create a service broker instance for testService */
		CircuitBreaker circuitBreaker = CircuitBreaker.of("targetService", circuitBreakerConfig());

		/* Decorate the target service call around circuit breaker */
		Supplier<String> supplier = CircuitBreaker.decorateSupplier(circuitBreaker, this::callService);

		Try<String> result = Try.ofSupplier(supplier);
		if (result.isSuccess()) {
			return result.get();
		} else {
			return "not reached the target service";
		}
	}

	public String testUsingCallable() throws InterruptedException, ExecutionException {

		/* create a service broker instance for testService */
		CircuitBreaker circuitBreaker = CircuitBreaker.of("targetService", circuitBreakerConfig());

		/* Decorate the target service call around circuit breaker */
		Callable<String> callable = CircuitBreaker.decorateCallable(circuitBreaker, this::callService);

		Future<String> result = Executors.newSingleThreadExecutor().submit(callable);
		try {
			return result.get();
		} catch (Exception e) { // Fail fast to protect resources from exhausting
			return "not reached the target service";
		}
	}

	public String callService() {
		return restTemplate.getForObject("http://localhost:8080/api/message", String.class);
	}

}
