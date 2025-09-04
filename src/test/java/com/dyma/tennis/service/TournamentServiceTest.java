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

import com.dyma.tennis.data.TournamentEntityList;
import com.dyma.tennis.exceptions.PlayerDataRetrievalException;
import com.dyma.tennis.exceptions.TournamentNotFoundException;
import com.dyma.tennis.model.Tournament;
import com.dyma.tennis.repository.TournamentRepository;

public class TournamentServiceTest {

  @Mock
  private TournamentRepository tournamentRepository;
  
  private TournamentService tournamentService;
  
  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    tournamentService = new TournamentService(tournamentRepository);
  }
  
  @Test
  public void shouldReturnAllTournaments() {
    // given
    Mockito.when(tournamentRepository.findAll()).thenReturn(TournamentEntityList.ALL);
    
    // when
    List<Tournament> alltournaments = tournamentService.getAllTournaments();
    
    // Then
    Assertions.assertThat(alltournaments)
      .extracting("name")
      .containsExactly("Australian Open", "French Open", "Wimbledon", "US Open");
  }
  
  @Test
  public void shouldFailReturnAlltournaments_whenDataAccessExceptionOccurs() {
    // Given
    Mockito.when(tournamentRepository.findAll()).thenThrow(new DataRetrievalFailureException("Data access error"));

    // When / Then
    Exception exception = assertThrows(PlayerDataRetrievalException.class, () -> {
      tournamentService.getAllTournaments();
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Could not retrieve tournament data");
  }
  
  @Test
  public void shouldRetrieveTournament() {
    // given
    UUID tournamentToRetrieve = UUID.fromString("a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12");
    Mockito.when(tournamentRepository.findOneByIdentifier(tournamentToRetrieve)).thenReturn(Optional.of(TournamentEntityList.FRENCH_OPEN));
    
    // When
    Tournament retrievedTournament = tournamentService.getByIdentifier(tournamentToRetrieve);
    
    // Then
    Assertions.assertThat(retrievedTournament.name()).isEqualTo("French Open");
  }
  
  @Test
  public void shouldFailtoRetrieveTournemant_whenTournamentDoesNotExist() {
    // Given
    String identifier = "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb";
    UUID unknownTournament = UUID.fromString(identifier);
    Mockito.when(tournamentRepository.findOneByIdentifier(unknownTournament)).thenReturn(Optional.empty());
  
    // When / Then
    Exception exception = assertThrows(TournamentNotFoundException.class, () -> {
      tournamentService.getByIdentifier(unknownTournament);
    });
    Assertions.assertThat(exception.getMessage()).isEqualTo("Tournament with identifier " + identifier + " not found !");
  }
}
