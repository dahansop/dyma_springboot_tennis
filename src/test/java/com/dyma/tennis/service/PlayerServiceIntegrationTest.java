package com.dyma.tennis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.model.Player;
import com.dyma.tennis.model.PlayerToSave;


@SpringBootTest
public class PlayerServiceIntegrationTest {

  @Autowired
  private PlayerService playerService;
  
  @BeforeEach
  void clearDatabase(@Autowired Flyway flyway) {
    flyway.clean();
    flyway.migrate();
  }
  
  @Test
  public void shoudlCeratePlayer() {
    // Given
    PlayerToSave playerToSave = new PlayerToSave("John", "Doe", LocalDate.of(2000, Month.JANUARY, 1), 10000);
    
    // When
    playerService.create(playerToSave);
    Player createdPlayer = playerService.getByLastName(playerToSave.lastName());
    
    // Then
    Assertions.assertThat(createdPlayer.firstName()).isEqualTo("John");
    Assertions.assertThat(createdPlayer.lastName()).isEqualTo("Doe");
    Assertions.assertThat(createdPlayer.birthDate()).isEqualTo(LocalDate.of(2000, Month.JANUARY, 1));
    Assertions.assertThat(createdPlayer.rank().points()).isEqualTo(10000);
    Assertions.assertThat(createdPlayer.rank().position()).isEqualTo(1);
  }
  
  @Test
  public void shouldUpdatePlayer() {
    // Given
    PlayerToSave playerToSave = new PlayerToSave("Rafael", "NadalTest", LocalDate.of(1986, Month.JUNE, 3), 1000);
    
    // When
    playerService.update(playerToSave);
    Player updatedPlayer = playerService.getByLastName(playerToSave.lastName());
    
    // Then
    Assertions.assertThat(updatedPlayer.rank().points()).isEqualTo(1000);
    Assertions.assertThat(updatedPlayer.rank().position()).isEqualTo(3);
  }
  
  @Test
  public void shouldDeletePlayer() {
    // Given
    String playerToDelete = "DjokovicTest";
    
    // when
    playerService.delete(playerToDelete);
    List<Player> allPlayers = playerService.getAllPlayers();
    
    // Then
    Assertions.assertThat(allPlayers)
    .extracting("lastName", "rank.position")
    .containsExactly(Tuple.tuple("NadalTest", 1), Tuple.tuple("FedererTest", 2));
  }
  
  @Test
  public void shouldFailDelete_whenPlayerDoesNotExist() {
    // Given
    String playerToDelete = "DoeTest";
    
    // when / Then
    Exception exception = assertThrows(PlayerNotFoundException.class, () -> {
      playerService.delete(playerToDelete);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("LastName " + playerToDelete + " not found !");
  }
}
