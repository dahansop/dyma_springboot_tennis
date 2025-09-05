package com.dyma.tennis.rest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.dyma.tennis.model.Player;
import com.dyma.tennis.model.PlayerToCreate;
import com.dyma.tennis.model.PlayerToUpdate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerEndtoEndTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @BeforeEach
  void clearDatabase(@Autowired Flyway flyway) {
    flyway.clean();
    flyway.migrate();
  }

  @Test
  public void shouldCreatePlayer() {
    // Given
    PlayerToCreate playerToCreate = new PlayerToCreate("Carlos", "Alcaraz", LocalDate.of(2003, Month.MAY, 5), 4500);

    // When
    String url = "http://localhost:" + port + "/players";
    HttpEntity<PlayerToCreate> request = new HttpEntity<>(playerToCreate);
    ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Player.class);

    // Then
    Assertions.assertThat(playerResponseEntity.getBody().info().lastName()).isEqualTo(playerToCreate.lastName());
    Assertions.assertThat(playerResponseEntity.getBody().info().rank().position()).isEqualTo(2);
  }

  @Test
  public void shouldFailToCreate_whenPlayerToCreateIsInvalid() {
    // Given
    PlayerToCreate playerToCreate = new PlayerToCreate("Carlos", null, LocalDate.of(2003, Month.MAY, 5), 4500);

    // When
    String url = "http://localhost:" + port + "/players";
    HttpEntity<PlayerToCreate> request = new HttpEntity<>(playerToCreate);
    ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Player.class);

    // Then
    Assertions.assertThat(playerResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  @Test
  public void shouldUpdatePlayerRanking() {
    // Given
    PlayerToUpdate playerToUpdate = new PlayerToUpdate(UUID.fromString("b466c6f7-52c6-4f25-b00d-c562be41311e"), "Rafael", "NadalTest", LocalDate.of(1986, Month.JUNE, 3), 1000);

    // When
    String url = "http://localhost:" + port + "/players";
    HttpEntity<PlayerToUpdate> request = new HttpEntity<>(playerToUpdate);
    ResponseEntity<Player> playerResponseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, request, Player.class);

    // Then
    Assertions.assertThat(playerResponseEntity.getBody().info().lastName()).isEqualTo(playerToUpdate.lastName());
    Assertions.assertThat(playerResponseEntity.getBody().info().rank().position()).isEqualTo(3);
  }

  @Test
  public void shouldDeletePlayer() {
    // Given
    String identifier = "d27aef45-51cd-401b-a04a-b78a1327b793";

    // when
    String urlDelete = "http://localhost:" + port + "/players/" + identifier;
    this.restTemplate.exchange(urlDelete, HttpMethod.DELETE, null, Void.class);

    String urlGet = "http://localhost:" + port + "/players";
    ResponseEntity<List<Player>> allPlayersResponseEntity = this.restTemplate.exchange(urlGet, HttpMethod.GET, null, new ParameterizedTypeReference<List<Player>>() {});

    //Then
    Assertions.assertThat(allPlayersResponseEntity.getBody())
      .extracting("info.lastName", "info.rank.position")
      .containsExactly(Tuple.tuple("NadalTest", 1), Tuple.tuple("FedererTest", 2));
  }
}
