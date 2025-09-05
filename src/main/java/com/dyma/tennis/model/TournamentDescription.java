package com.dyma.tennis.model;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TournamentDescription(
    @NotNull(message = "L'Identifier est obligatoire") UUID identifier,
    @NotBlank(message = "Le name est obligatoire") String name,
    @NotNull(message = "La startDate est obligatoire") LocalDate startDate,
    @NotNull(message = "La endDate est obligatoire") LocalDate endDate,
    @Positive(message = "La prime doit avoir une valeur positive") Integer prizeMoney,
    @NotNull(message = "La capacity est obligatoire") @Positive(message = "La capacité doit avoir une valeur positive") Integer capacity) {

}
