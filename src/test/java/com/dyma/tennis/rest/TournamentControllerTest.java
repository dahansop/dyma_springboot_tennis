package com.dyma.tennis.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dyma.tennis.data.TournamentList;
import com.dyma.tennis.exceptions.TournamentNotFoundException;
import com.dyma.tennis.service.RegistrationService;
import com.dyma.tennis.service.TournamentService;

@WebMvcTest(controllers = TournamentController.class)
public class TournamentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TournamentService tournamentService;

  @MockitoBean
  private RegistrationService registrationService;

  @Test
  public void shouldListAllTournaments() throws Exception {
    // Given
    Mockito.when(tournamentService.getAllTournaments()).thenReturn(TournamentList.ALL);

    // when / then
    mockMvc.perform(get("/tournaments"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$", hasSize(4)))
    .andExpect(jsonPath("$[0].info.name", CoreMatchers.is("Australian Open")))
    .andExpect(jsonPath("$[1].info.name", CoreMatchers.is("French Open")))
    .andExpect(jsonPath("$[2].info.name", CoreMatchers.is("Wimbledon")))
    .andExpect(jsonPath("$[3].info.name", CoreMatchers.is("US Open")));
  }

  @Test
  public void shouldRetrieveTournament() throws Exception {
    // Given
    String identifier = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12";
    UUID tournamentToRetrieve = UUID.fromString(identifier);
    Mockito.when(tournamentService.getByIdentifier(tournamentToRetrieve)).thenReturn(TournamentList.FRENCH_OPEN);

    // When / then
    mockMvc.perform(get("/tournaments/" + identifier))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.info.name", CoreMatchers.is("French Open")));
  }

  @Test
  public void shouldReturn404NotFound_whenTournamentDoesNotExist() throws Exception {
    // Given
    String identifier = "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12";
    UUID tournamentToRetrieve = UUID.fromString(identifier);
    Mockito.when(tournamentService.getByIdentifier(tournamentToRetrieve)).thenThrow(new TournamentNotFoundException(tournamentToRetrieve));

    // When / then
    mockMvc.perform(get("/tournaments/" + identifier))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errorDetails", CoreMatchers.is("Tournament with identifier " + identifier + " not found !")));
  }
}
