package com.dyma.tennis.model;

/**
 * Objet représentant l'état de santé de l'application
 */
public record HealthCheck(ApplicationStatus status, String message) {

}
