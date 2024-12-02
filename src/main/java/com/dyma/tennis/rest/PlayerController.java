package com.dyma.tennis.rest;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.dto.Player;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * On ne peut pas avoir 2 joueurs avec le même nom
 */
@Tag(name = "Tennis players API")
@RestController
@RequestMapping("/players")
public class PlayerController {

  @Operation(summary = "Liste des joueurs", description = "Liste des joueurs")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "la liste des joueurs", content = {
      @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Player.class))) }) })
  @GetMapping
  public List<Player> list() {
    return Collections.emptyList();
  }

  @Operation(summary = "Recherche un joueur", description = "Recherche un joueur a partir de son nom")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "un joueur", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class)) }) })
  @GetMapping("{lastName}")
  public Player getByLastName(@PathVariable String lastName) {
    return null;
  }

  @Operation(summary = "Créer un joueur", description = "Créer un joueur")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "le joueur créé", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class)) }) })
  @PostMapping
  public Player createPlayer(@RequestBody Player player) {
    return player;
  }

  @Operation(summary = "Mise à jour d'un joueur", description = "Mise à jour d'un joueur")
  @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "le joueur mis à jour", content = {
      @Content(mediaType = "application/json", schema = @Schema(implementation = Player.class)) }) })
  @PutMapping
  public Player updatePlayer(@RequestBody Player player) {
    return player;
  }

  @Operation(summary = "Suppression d'un joueur", description = "Suppression d'un joueur à partir de son nom")
  @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "le joueur a été supprimé") })
  @DeleteMapping("{lastName}")
  public void deletePlayerByLastName(@PathVariable("lastName") String lastName) {

  }
}
