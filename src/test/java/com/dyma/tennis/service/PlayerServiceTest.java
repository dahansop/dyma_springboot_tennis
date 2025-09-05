package com.dyma.tennis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataRetrievalFailureException;

import com.dyma.tennis.data.PlayerEntityList;
import com.dyma.tennis.exceptions.PlayerDataRetrievalException;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.mapper.PlayerMapper;
import com.dyma.tennis.model.Player;
import com.dyma.tennis.repository.PlayerRepository;

public class PlayerServiceTest {

  @Mock
  private PlayerRepository playerRepository;

  private PlayerService playerService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    playerService = new PlayerService(playerRepository, new PlayerMapper());
  }

  @Test
  public void shouldReturnPlayerRanking() {
    // given
    Mockito.when(playerRepository.findAll()).thenReturn(PlayerEntityList.ALL);

    // when
    List<Player> allPlayers = playerService.getAllPlayers();

    // Then
    Assertions.assertThat(allPlayers)
      .extracting("info.lastName")
      .containsExactly("Nadal", "Djokovic", "Federer", "Murray");
  }

  @Test
  public void shouldFailReturnPlayersRanking_whenDataAccessExceptionOccurs() {
    // Given
    Mockito.when(playerRepository.findAll()).thenThrow(new DataRetrievalFailureException("Data access error"));

    // When / Then
    Exception exception = assertThrows(PlayerDataRetrievalException.class, () -> {
      playerService.getAllPlayers();
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Could not retrieve player data");
  }

  @Test
  public void shouldRetrievePlayer() {
    // given
    UUID playerToRetrieve = UUID.fromString("aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb");
    Mockito.when(playerRepository.findOneByIdentifier(playerToRetrieve)).thenReturn(Optional.of(PlayerEntityList.RAFAEL_NADAL));

    // When
    Player retrievePlayer = playerService.getByIdentifier(playerToRetrieve);

    // then
    Assertions.assertThat(retrievePlayer.info().lastName()).isEqualTo("Nadal");
    Assertions.assertThat(retrievePlayer.info().firstName()).isEqualTo("Rafael");
    Assertions.assertThat(retrievePlayer.info().rank().position()).isEqualTo(1);
  }

  @Test
  public void shouldFailtoRetrievePlayer_whenPlayerDoesNotExist() {
    // Given
    String identifier = "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb";
    UUID unknownPlayer = UUID.fromString(identifier);
    Mockito.when(playerRepository.findOneByIdentifier(unknownPlayer)).thenReturn(Optional.empty());

    // When / Then
    Exception exception = assertThrows(PlayerNotFoundException.class, () ->{
      playerService.getByIdentifier(unknownPlayer);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Player with identifier " + identifier + " not found !");
  }
}
