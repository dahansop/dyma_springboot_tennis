package com.dyma.tennis.model;

import java.util.Set;

import jakarta.validation.Valid;

public record Tournament(
    @Valid TournamentDescription info,
    @Valid Set<PlayerDescription> players) {

}
