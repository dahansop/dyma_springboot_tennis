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
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dyma.tennis.data.PlayerList;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.security.SecurityConfiguration;
import com.dyma.tennis.service.PlayerService;

@WebMvcTest(controllers = PlayerController.class)
@Import(SecurityConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class PlayerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private PlayerService playerService;

  @Test
  public void shouldListAllPlayers() throws Exception {
    // Given
    Mockito.when(playerService.getAllPlayers()).thenReturn(PlayerList.ALL);

    // When / then
    mockMvc.perform(get("/players"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$", hasSize(4)))
    .andExpect(jsonPath("$[0].info.lastName", CoreMatchers.is("Nadal")))
    .andExpect(jsonPath("$[1].info.lastName", CoreMatchers.is("Djokovic")))
    .andExpect(jsonPath("$[2].info.lastName", CoreMatchers.is("Federer")))
    .andExpect(jsonPath("$[3].info.lastName", CoreMatchers.is("Murray")));
  }

  @Test
  public void shouldBeRetrievePLayer() throws Exception {
    // Given
    String identifier = "b466c6f7-52c6-4f25-b00d-c562be41311e";
    UUID playerToRetrieve = UUID.fromString(identifier);
    Mockito.when(playerService.getByIdentifier(playerToRetrieve)).thenReturn(PlayerList.RAFAEL_NADAL);

    // When / then
    mockMvc.perform(get("/players/" + identifier))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.info.lastName", CoreMatchers.is("Nadal")))
      .andExpect(jsonPath("$.info.rank.position", CoreMatchers.is(1)));
  }

  @Test
  public void shouldReturn404NotFound_whenPLayerDoesNotExist() throws Exception {
    // Given
    String identifier = "aaaaaaaa-1111-2222-3333-bbbbbbbbbbbb";
    UUID playerToRetrieve = UUID.fromString(identifier);
    Mockito.when(playerService.getByIdentifier(playerToRetrieve)).thenThrow(new PlayerNotFoundException(playerToRetrieve));

    // When / then
    mockMvc.perform(get("/players/" + identifier))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errorDetails", CoreMatchers.is("Player with identifier " + identifier + " not found !")));
  }
}
