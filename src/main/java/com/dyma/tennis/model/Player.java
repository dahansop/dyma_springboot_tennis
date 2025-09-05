package com.dyma.tennis.model;

import java.util.Set;

import jakarta.validation.Valid;

/**
 * Représente un joueur
 */
public record Player(
    @Valid PlayerDescription info,
    @Valid Set<TournamentDescription> tournaments) {
}
