package com.dyma.tennis.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.dyma.tennis.data.PlayerList;
import com.dyma.tennis.exceptions.PlayerNotFoundException;
import com.dyma.tennis.service.PlayerService;

@WebMvcTest(controllers = PlayerController.class)
public class PlayerControllerTest {

  @Autowired
  private MockMvc mockMvc;
  
  @MockitoBean
  private PlayerService playerService;
  
  @Test
  public void shouldListAllPlayers() throws Exception {
    // Given
    Mockito.when(playerService.getAll()).thenReturn(PlayerList.ALL);
    
    // When / then
    mockMvc.perform(get("/players"))
    .andExpect(status().isOk())
    .andExpect(jsonPath("$", hasSize(4)))
    .andExpect(jsonPath("$[0].lastName", CoreMatchers.is("Nadal")))
    .andExpect(jsonPath("$[1].lastName", CoreMatchers.is("Djokovic")))
    .andExpect(jsonPath("$[2].lastName", CoreMatchers.is("Federer")))
    .andExpect(jsonPath("$[3].lastName", CoreMatchers.is("Murray")));
  }
  
  @Test
  public void shouldBeRetrievePLayer() throws Exception {
    // Given
    String playertoRetrieve = "nadal";
    Mockito.when(playerService.getByLastName(playertoRetrieve)).thenReturn(PlayerList.RAFAEL_NADAL);
    
    // When / then
    mockMvc.perform(get("/players/" + playertoRetrieve))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.lastName", CoreMatchers.is("Nadal")))
      .andExpect(jsonPath("$.rank.position", CoreMatchers.is(1)));
  }
  
  @Test
  public void shouldReturn404NotFound_whenPLayerDoesNotExist() throws Exception {
    // Given
    String playerToRetrieve = "doe";
    Mockito.when(playerService.getByLastName(playerToRetrieve)).thenThrow(new PlayerNotFoundException(playerToRetrieve));
    
    // When / then
    mockMvc.perform(get("/players/" + playerToRetrieve))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.errorDetails", CoreMatchers.is("LastName " + playerToRetrieve + " not found !")));
  }
}
