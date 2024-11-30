package com.dyma.tennis.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.dto.HealthCheck;
import com.dyma.tennis.service.HealthCheckService;

@RestController
public class HealthCheckController {

	@Autowired
	private HealthCheckService healthCheckService;
	
	@GetMapping("/healthcheck")
	public HealthCheck healthCheck() {
		return healthCheckService.healthCheck();
	}
}
