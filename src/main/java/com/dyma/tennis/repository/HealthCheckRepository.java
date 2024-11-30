package com.dyma.tennis.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;

@Repository
public class HealthCheckRepository {

	@Autowired
	private EntityManager entityManager;
	
	/**
	 * 
	 * @return Le nombre de connexion ouverte pour l'application
	 */
	public Long countApplicationConection() {
	  String query = "select count(1) from pg_stat_activity where application_name = 'PostgreSQL JDBC Driver'";
	  return (Long) entityManager.createNativeQuery(query).getSingleResult();
	}
}
