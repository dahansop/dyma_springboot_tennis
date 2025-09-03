package com.dyma.tennis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dyma.tennis.model.ApplicationStatus;
import com.dyma.tennis.model.HealthCheck;
import com.dyma.tennis.repository.HealthCheckRepository;

/**
 * Service pour la gestion de l'état de santé de l'application
 */
@Service
public class HealthCheckService {

  @Autowired
  private HealthCheckRepository healthCheckRepository;
  
	public HealthCheck healthCheck() {
		Long nbAppConnection = healthCheckRepository.countApplicationConection();
		if (nbAppConnection > 0) {
		  return new HealthCheck(ApplicationStatus.OK, "Welcome to Tennis Game!");
		} else {
		  return new HealthCheck(ApplicationStatus.KO, "Error : datasource not connected !");
		}
	}
}
