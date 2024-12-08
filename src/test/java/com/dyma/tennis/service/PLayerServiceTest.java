package com.dyma.tennis.service;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.dyma.tennis.data.PlayerEntityList;
import com.dyma.tennis.dto.Player;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.repository.PlayerRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class PLayerServiceTest {

  @Mock
  private PlayerRepository playerRepository;
  
  private PlayerService playerService;
  
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    playerService = new PlayerService(playerRepository);
  }
  
  @Test
  public void shouldReturnPlayerRanking() {
    // given
    Mockito.when(playerRepository.findAll()).thenReturn(PlayerEntityList.ALL);
    
    // when
    List<Player> allPlayers = playerService.getAll();
    
    // Then
    Assertions.assertThat(allPlayers)
      .extracting("lastName")
      .containsExactly("Nadal", "Djokovic", "Federer", "Murray");
  }
  
  @Test
  public void shouldRetrievePlayer() {
    // given
    String playerToRetrieve = "nadal";
    Mockito.when(playerRepository.findOneByLastNameIgnoreCase(playerToRetrieve)).thenReturn(Optional.of(PlayerEntityList.RAFAEL_NADAL));
    
    // When
    Player retrievePlayer = playerService.getByLastName(playerToRetrieve);
    
    // then
    Assertions.assertThat(retrievePlayer.lastName()).isEqualTo("Nadal");
    Assertions.assertThat(retrievePlayer.firstName()).isEqualTo("Rafael");
    Assertions.assertThat(retrievePlayer.rank().position()).isEqualTo(1);
  }
  
  @Test
  public void shouldFailtoRetrievePlayer_whenPlayerDoesNotExist() {
    // Given
    String unknownPlayer = "doe";
    Mockito.when(playerRepository.findOneByLastNameIgnoreCase(unknownPlayer)).thenReturn(Optional.empty());
  
    // When / Then
    Exception exception = assertThrows(PlayerNotFoundException.class, () ->{
      playerService.getByLastName(unknownPlayer);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("LastName " + unknownPlayer + " not found !");
  }
}
