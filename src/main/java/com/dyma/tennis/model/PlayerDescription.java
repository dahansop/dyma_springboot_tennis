package com.dyma.tennis.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

/**
 * Représente un joueur
 */
public record PlayerDescription(
    @NotNull(message = "L'Identifier est obligatoire") UUID identifier,
    @NotBlank(message = "Le firstName est obligatoire") String firstName,
    @NotBlank(message = "Le lastName est obligatoire") String lastName,
    @NotNull(message = "La birthDate est obligatoire") @PastOrPresent(message = "La birthDate doit etre dans le passe ou le present") LocalDate birthDate,
    @Valid Rank rank) {

}
