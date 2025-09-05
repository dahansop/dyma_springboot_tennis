package com.dyma.tennis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.dyma.tennis.data.PlayerEntityList;
import com.dyma.tennis.data.TournamentEntityList;
import com.dyma.tennis.exceptions.TournamentRegistrationException;
import com.dyma.tennis.repository.PlayerRepository;
import com.dyma.tennis.repository.TournamentRepository;

public class RegistrationServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  @Mock
  private TournamentRepository tournamentRepository;

  private RegistrationService registrationService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    registrationService = new RegistrationService(tournamentRepository, playerRepository);
  }

  @Test
  public void shouldRegisterPlayerToTournament() {
    // Given
    UUID frenchOpen = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
    Mockito.when(tournamentRepository.findOneByIdentifier(frenchOpen)).thenReturn(Optional.of(TournamentEntityList.FRENCH_OPEN));

    UUID rafaelNadal = UUID.fromString("b466c6f7-52c6-4f25-b00d-c562be41311e");
    Mockito.when(playerRepository.findOneByIdentifier(rafaelNadal)).thenReturn(Optional.of(PlayerEntityList.RAFAEL_NADAL));

    // When
    registrationService.register(frenchOpen, rafaelNadal);

    // Then
    Mockito.verify(playerRepository).save(PlayerEntityList.RAFAEL_NADAL);
  }

  @Test
  public void shouldFailToRegister_whenTournamentIsNotFound() {
    // Given
    String tournamentIdentifier = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12";
    UUID frenchOpen = UUID.fromString(tournamentIdentifier);
    Mockito.when(tournamentRepository.findOneByIdentifier(frenchOpen)).thenReturn(Optional.empty());

    UUID rafaelNadal = UUID.fromString("b466c6f7-52c6-4f25-b00d-c562be41311e");
    Mockito.when(playerRepository.findOneByIdentifier(rafaelNadal)).thenReturn(Optional.of(PlayerEntityList.RAFAEL_NADAL));

    // When / Then
    Exception exception = assertThrows(TournamentRegistrationException.class, () -> {
      registrationService.register(frenchOpen, rafaelNadal);
    });

    Assertions.assertThat(exception.getMessage()).isEqualTo("Tournament with identifier " + tournamentIdentifier + " does not exist");
  }

  @Test
  public void shouldFailToRegister_whenPlayerIsNotFound() {
    // Given
    String tournamentIdentifier = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12";
    UUID frenchOpen = UUID.fromString(tournamentIdentifier);
    Mockito.when(tournamentRepository.findOneByIdentifier(frenchOpen)).thenReturn(Optional.of(TournamentEntityList.FRENCH_OPEN));

    String playerIdentifier = "b466c6f7-52c6-4f25-b00d-c562be41311e";
    UUID rafaelNadal = UUID.fromString(playerIdentifier);
    Mockito.when(playerRepository.findOneByIdentifier(rafaelNadal)).thenReturn(Optional.empty());

    // When / Then
    Exception exception = assertThrows(TournamentRegistrationException.class, () -> {
      registrationService.register(frenchOpen, rafaelNadal);
    });

    Assertions.assertThat(exception.getMessage()).isEqualTo("Player with identifier " + playerIdentifier + " does not exist");
  }
}
