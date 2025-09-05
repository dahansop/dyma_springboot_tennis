package com.dyma.tennis.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

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

import com.dyma.tennis.exceptions.TournamentAlreadyExistsException;
import com.dyma.tennis.exceptions.TournamentNotFoundException;
import com.dyma.tennis.model.Tournament;
import com.dyma.tennis.model.TournamentToCreate;
import com.dyma.tennis.model.TournamentToUpdate;

@SpringBootTest
public class TournamentServiceIntegrationTest {

  @Autowired
  private TournamentService tournamentService;

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
        64);

    // When
    Tournament savedTournament = tournamentService.create(tournamentToCreate);
    Tournament createdTournament = tournamentService.getByIdentifier(savedTournament.info().identifier());

    // Then
    Assertions.assertThat(createdTournament.info().name()).isEqualTo("Madrid Master 1000");
    Assertions.assertThat(createdTournament.info().startDate()).isEqualTo(startDate);
    Assertions.assertThat(createdTournament.info().endDate()).isEqualTo(endDate);
    Assertions.assertThat(createdTournament.info().prizeMoney()).isEqualTo(500000);
    Assertions.assertThat(createdTournament.info().capacity()).isEqualTo(64);
  }

  @Test
  public void shouldFailToCreateanExistingTournament() {
    // Given
    TournamentToCreate tournamentToCreate = new TournamentToCreate(
        "Madrid Master 1000",
        LocalDate.now().plusDays(10),
        LocalDate.now().plusDays(17),
        500000,
        64
    );
    tournamentService.create(tournamentToCreate);

    TournamentToCreate duplicatedTournamentToCreate = new TournamentToCreate(
        "Madrid Master 1000",
        LocalDate.now().plusDays(10),
        LocalDate.now().plusDays(17),
        500000,
        64
);

    // When / then
    Exception exception = assertThrows(TournamentAlreadyExistsException.class, () -> {
      tournamentService.create(duplicatedTournamentToCreate);
    });
    Assertions.assertThat(exception.getMessage()).contains("Tournament with name Madrid Master 1000 already exists.");
  }

  @Test
  public void shouldUpdateTournament() {
    // Given
    UUID frenchOpenIdentifier = UUID.fromString("d4a9f8e2-9051-4739-90bc-1cb7e4c7ad42");
    LocalDate startDate = LocalDate.of(2025, Month.MAY, 26);
    LocalDate endDate = LocalDate.of(2025, Month.JUNE, 9);
    TournamentToUpdate tournamentToUpdate = new TournamentToUpdate(
        frenchOpenIdentifier,
        "Roland Garros",
        startDate,
        endDate,
        2500000,
        128);

    // when
    tournamentService.update(tournamentToUpdate);
    Tournament updatedTournament = tournamentService.getByIdentifier(frenchOpenIdentifier);

    // Then
    Assertions.assertThat(updatedTournament.info().name()).isEqualTo("Roland Garros");
    Assertions.assertThat(updatedTournament.info().startDate()).isEqualTo(startDate);
    Assertions.assertThat(updatedTournament.info().endDate()).isEqualTo(endDate);
    Assertions.assertThat(updatedTournament.info().prizeMoney()).isEqualTo(2500000);
    Assertions.assertThat(updatedTournament.info().capacity()).isEqualTo(128);
  }

  @Test
  public void shouldDeleteTournament() {
    // given
    UUID tournamentToDelete = UUID.fromString("124edf07-64fa-4ea4-a65e-3bfe96df5781");

    // when
    tournamentService.delete(tournamentToDelete);

    // Then
    List<Tournament> alltournaments = tournamentService.getAllTournaments();
    Assertions.assertThat(alltournaments)
      .extracting("info.name")
      .containsExactly("Australian Open", "French Open", "Wimbledon");
  }

  @Test
  public void shouldFailDelete_whenTournamentDoesNotExist() {
    // Given
    UUID tournamentToDelete = UUID.fromString("5f8c9b43-8d74-49e8-b821-f43d57e4a9b7");

    // When / then
    Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
      tournamentService.delete(tournamentToDelete);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Tournament with identifier " + tournamentToDelete + " not found !");
  }
}
