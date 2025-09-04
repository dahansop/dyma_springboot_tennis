package com.dyma.tennis.rest;

import java.util.List;
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

import com.dyma.tennis.model.Player;
import com.dyma.tennis.model.PlayerToCreate;
import com.dyma.tennis.model.PlayerToUpdate;
import com.dyma.tennis.service.PlayerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * API REST des joueurs de tennis
 * On ne peut pas avoir 2 joueurs avec le même nom
 */
@Tag(name = "Tennis players API")
@RestController
@RequestMapping("/players")
public class PlayerController {

  @Autowired
  private PlayerService playerService;
  
  @Operation(summary = "Liste des joueurs", description = "Liste des joueurs")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "la liste des joueurs", 
         content = {@Content(mediaType = "application/json",
         array = @ArraySchema(schema = @Schema(implementation = Player.class))) }),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @GetMapping
  public List<Player> list() {
    return playerService.getAllPlayers();
  }

  @Operation(summary = "Recherche un joueur", description = "Recherche un joueur a partir de son identifiant")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "un joueur", 
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Player.class))}),
      @ApiResponse(responseCode = "404", description = "Joueur non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @GetMapping("{identifier}")
  public Player getPlayer(@PathVariable("identifier") UUID identifier) {
    return playerService.getByIdentifier(identifier);
  }

  @Operation(summary = "Créer un joueur", description = "Créer un joueur")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "le joueur créé",
          content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = Player.class)) }),
      @ApiResponse(responseCode = "400", description = "Le joueur existe déjà",
          content = {@Content(mediaType = "application/json", 
          schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @PostMapping
  public Player createPlayer(@RequestBody @Valid PlayerToCreate playerToCreate) {
    return playerService.create(playerToCreate);
  }

  @Operation(summary = "Mise à jour d'un joueur", description = "Mise à jour d'un joueur")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "le joueur mis à jour", 
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Player.class)) }),
      @ApiResponse(responseCode = "404", description = "Joueur non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "400", description = "Information manquante ou illisible",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @PutMapping
  public Player updatePlayer(@RequestBody @Valid PlayerToUpdate playerToUpdate) {
    return playerService.update(playerToUpdate);
  }

  @Operation(summary = "Suppression d'un joueur", description = "Suppression d'un joueur à partir de son nom")
  @ApiResponses(value = { 
      @ApiResponse(responseCode = "200", description = "le joueur a été supprimé"),
      @ApiResponse(responseCode = "404", description = "Joueur non trouvé",
         content = {@Content(mediaType = "application/json", 
         schema = @Schema(implementation = Error.class))}),
      @ApiResponse(responseCode = "403", description = "l'utilisateur n'est pas autorisé")
  })
  @DeleteMapping("{identifier}")
  public void deletePlayer(@PathVariable("identifier") UUID identifier) {
    playerService.delete(identifier);
  }
}
