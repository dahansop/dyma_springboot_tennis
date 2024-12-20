package com.dyma.tennis.dto;

import jakarta.validation.constraints.NotBlank;

public record UserCredentials(
    @NotBlank(message = "Le login est obligatoire") String login, 
    @NotBlank(message = "Le password est obligatoire") String password) {
}
