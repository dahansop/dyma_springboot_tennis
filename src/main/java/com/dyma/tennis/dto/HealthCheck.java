package com.dyma.tennis.dto;

/**
 * Objet représentant l'état de santé de l'application
 */
public record HealthCheck(ApplicationStatus status, String message) {

}
