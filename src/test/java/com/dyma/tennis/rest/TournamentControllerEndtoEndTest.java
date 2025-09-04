package com.dyma.tennis.rest;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;
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

import com.dyma.tennis.model.Tournament;
import com.dyma.tennis.model.TournamentToCreate;
import com.dyma.tennis.model.TournamentToUpdate;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TournamentControllerEndtoEndTest {

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
  public void shouldCreateTournament() {
    // Given
    LocalDate startDate = LocalDate.now().plusDays(10);
    LocalDate endDate = LocalDate.now().plusDays(17);
    TournamentToCreate tournamentToCreate = new TournamentToCreate(
        "Madrid Master 1000",
        startDate,
        endDate,
        500000,
        64
    );
    
    // When
    String url = "http://localhost:" + port + "/tournaments";
    HttpEntity<TournamentToCreate> request = new HttpEntity<>(tournamentToCreate);
    ResponseEntity<Tournament> tournamentResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Tournament.class);
    
    // Then
    Assertions.assertThat(tournamentResponseEntity.getBody().name()).isEqualTo("Madrid Master 1000");
    Assertions.assertThat(tournamentResponseEntity.getBody().startDate()).isEqualTo(startDate);
    Assertions.assertThat(tournamentResponseEntity.getBody().endDate()).isEqualTo(endDate);
    Assertions.assertThat(tournamentResponseEntity.getBody().prizeMoney()).isEqualTo(500000);
    Assertions.assertThat(tournamentResponseEntity.getBody().capacity()).isEqualTo(64);
  }
  
  @Test
  public void shouldFailToCreate_whenTournamentToCreateIsInvalid() {
    // Given
    TournamentToCreate tournamentToCreate = new TournamentToCreate(null, LocalDate.now().plusDays(10), LocalDate.now().plusDays(17), 500000, 64);
    
    // When
    String url = "http://localhost:" + port + "/tournaments";
    HttpEntity<TournamentToCreate> request = new HttpEntity<>(tournamentToCreate);
    ResponseEntity<Tournament> tournamentResponseEntity = this.restTemplate.exchange(url, HttpMethod.POST, request, Tournament.class);
    
    // Then
    Assertions.assertThat(tournamentResponseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }
  
  @Test
  public void shouldUpdateTournament() {
    // Given
    LocalDate startDate = LocalDate.of(2025, Month.MAY, 26);
    LocalDate endDate = LocalDate.of(2025, Month.JUNE, 9);
    TournamentToUpdate tournamentToUpdate = new TournamentToUpdate(
        UUID.fromString("d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42"),
        "Roland Garros",
        startDate,
        endDate,
        2500000,
        128);
    
    // When
    String url = "http://localhost:" + port + "/tournaments";
    HttpEntity<TournamentToUpdate> request = new HttpEntity<>(tournamentToUpdate);
    ResponseEntity<Tournament> tournamentResponseEntity = this.restTemplate.exchange(url, HttpMethod.PUT, request, Tournament.class);
    
    // Then
    Assertions.assertThat(tournamentResponseEntity.getBody().name()).isEqualTo("Roland Garros");
    Assertions.assertThat(tournamentResponseEntity.getBody().startDate()).isEqualTo(startDate);
    Assertions.assertThat(tournamentResponseEntity.getBody().endDate()).isEqualTo(endDate);
    Assertions.assertThat(tournamentResponseEntity.getBody().prizeMoney()).isEqualTo(2500000);
    Assertions.assertThat(tournamentResponseEntity.getBody().capacity()).isEqualTo(128);
  }
  
  @Test
  public void shouldDeleteTournament() {
    // Given
    String identifier = "124edf07-64fa-4ea4-a65e-3bfe96df5781";
    
    // when
    String urlDelete = "http://localhost:" + port + "/tournaments/" + identifier;
    this.restTemplate.exchange(urlDelete, HttpMethod.DELETE, null, Void.class);
    
    String urlGet = "http://localhost:" + port + "/tournaments";
    ResponseEntity<List<Tournament>> allTournamentsResponseEntity = this.restTemplate.exchange(urlGet, HttpMethod.GET, null, new ParameterizedTypeReference<List<Tournament>>() {});
    
    //Then
    Assertions.assertThat(allTournamentsResponseEntity.getBody())
      .extracting("name")
      .containsExactly("Australian Open", "French Open", "Wimbledon");
  }
}
