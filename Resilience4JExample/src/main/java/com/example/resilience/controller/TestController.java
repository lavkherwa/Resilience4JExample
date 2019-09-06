package com.example.resilience.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.resilience.service.TestDecorator;

@RestController
@RequestMapping("/")
public class TestController {

	final TestDecorator testDecorator;

	public TestController(TestDecorator testService) {
		this.testDecorator = testService;
	}

	@GetMapping("test")
	public String testUsingSupplier() {
		return testDecorator.testUsingSupplier();
	}

}
