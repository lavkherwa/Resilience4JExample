package com.example.resilience.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience.service.TestService;

@RestController
@RequestMapping("/")
public class TestController {

	final TestService testService;

	public TestController(TestService testService) {
		this.testService = testService;
	}

	@GetMapping("test")
	public String testUsingSupplier() {
		return testService.testUsingSupplier();
	}

}
