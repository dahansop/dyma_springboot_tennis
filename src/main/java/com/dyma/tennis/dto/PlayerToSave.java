package com.dyma.tennis.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Représente un joueur
 */
public record PlayerToSave(
    @NotBlank(message = "Le firstName est obligatoire") String firstName, 
    @NotBlank(message = "Le lastName est obligatoire") String lastName, 
    @NotNull(message = "La birthDate est obligatoire") @PastOrPresent(message = "La birthDate doit etre dans le passe ou le present") LocalDate birthDate, 
    @PositiveOrZero(message = "Le nombre de points doit etre superieure ou egal 0") int points) {

}
