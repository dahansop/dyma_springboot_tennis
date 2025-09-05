package com.dyma.tennis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dyma.tennis.exceptions.TournamentRegistrationException;
import com.dyma.tennis.model.Player;

@SpringBootTest
public class RegistrationServiceIntegrationTest {

  @Autowired
  private RegistrationService registrationService;

  @Autowired
  private PlayerService playerService;

  @BeforeEach
  void clearDatabase(@Autowired Flyway flyway) {
    flyway.clean();
    flyway.migrate();
  }

  @Test
  public void shouldRegisterPlayerToTournament() {
    // Given
    UUID frenchOpen = UUID.fromString("d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42");
    UUID rafaelNadal = UUID.fromString("b466c6f7-52c6-4f25-b00d-c562be41311e");

    // When
    registrationService.register(frenchOpen, rafaelNadal);
    Player updatedPlayer = playerService.getByIdentifier(rafaelNadal);

    // Then
    Assertions.assertThat(updatedPlayer.tournaments()).extracting("name").containsExactly("French Open");
  }

  @Test
  public void shouldFailRegisterPlayer_WhenPlayerIsAlreadyRegistered() {
    // Given
    String tournamentIdentifier = "d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42";
    UUID frenchOpen = UUID.fromString(tournamentIdentifier);
    String playerIdentifier = "b466c6f7-52c6-4f25-b00d-c562be41311e";
    UUID rafaelNadal = UUID.fromString(playerIdentifier);
    registrationService.register(frenchOpen, rafaelNadal);

    // When / Then
    Exception exception = assertThrows(TournamentRegistrationException.class, () -> {
      registrationService.register(frenchOpen, rafaelNadal);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Player with identifier " + playerIdentifier + " is already registred to tournament identifier " + tournamentIdentifier);
  }
}
