package com.dyma.tennis.rest;import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.model.Tournament;
import com.dyma.tennis.model.TournamentToCreate;
import com.dyma.tennis.model.TournamentToUpdate;
import com.dyma.tennis.service.TournamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * API REST des tournois de tennis
 * 
 */
@Tag(name = "Tournaments API")
@RestController
@RequestMapping("/tournaments")
public class TournamentController {

  @Autowired
  private TournamentService tournamentService;
  
  @Operation(summary = "Liste des tournois", description = "Liste des tournois")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "la liste des tournois", 
         content = {@Content(mediaType = "application/json",
         array = @ArraySchema(schema = @Schema(implementation = Tournament.class))) }),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @GetMapping
  public List<Tournament> list() {
    return tournamentService.getAllTournaments();
  }
  
  @Operation(summary = "Recherche un tournois", description = "Recherche un tournois a partir de son identifiant")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "un tournois", 
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Tournament.class))}),
      @ApiResponse(responseCode = "404", description = "Tournois non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @GetMapping("{identifier}")
  public Tournament getTournament(@PathVariable("identifier") UUID identifier) {
    return tournamentService.getByIdentifier(identifier);
  }
  
  @Operation(summary = "Créer un tournois", description = "Créer un tournois")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "le tournois créé",
          content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = Tournament.class)) }),
      @ApiResponse(responseCode = "400", description = "Le tournois existe déjà",
          content = {@Content(mediaType = "application/json", 
          schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @PostMapping
  public Tournament createTournament(@RequestBody @Valid TournamentToCreate tournamentToCreate) {
    return tournamentService.create(tournamentToCreate);
  }
  
  @Operation(summary = "Mise à jour d'un tournois", description = "Mise à jour d'un tournois")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "le tournois mis à jour", 
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Tournament.class)) }),
      @ApiResponse(responseCode = "404", description = "Tournois non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "400", description = "Information manquante ou illisible",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @PutMapping
  public Tournament updateTournament(@RequestBody @Valid TournamentToUpdate tournamentToUpdate) {
    return tournamentService.update(tournamentToUpdate);
  }

  @Operation(summary = "Suppression d'un tournois", description = "Suppression d'un tournois à partir de son nom")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "le tournois a été supprimé"),
      @ApiResponse(responseCode = "404", description = "Tournois non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @DeleteMapping("{identifier}")
  public void deleteTournament(@PathVariable("identifier") UUID identifier) {
    tournamentService.delete(identifier);
  }
}
