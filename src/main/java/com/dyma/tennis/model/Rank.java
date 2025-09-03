package com.dyma.tennis.model;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Représentante un classement
 */
public record Rank(
    @Positive(message = "La position est strictement positive") int position, 
    @PositiveOrZero(message = "Le nombre de points doit etre superieure ou egal 0") int points) {

}
