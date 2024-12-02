package com.dyma.tennis.dto;

import java.time.LocalDate;

public record Player(String firstName, String lastName, LocalDate birthDate, Rank rank) {

}
