package com.dyma.tennis.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.model.UserAuthentication;
import com.dyma.tennis.model.UserCredentials;
import com.dyma.tennis.security.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

/**
 * API de la gestion des comptes
 */
@Tag(name = "accounts API")
@RestController
@RequestMapping("/accounts")
public class AccountController {

  @Autowired
  private AuthenticationManager authenticationManager;
  
  @Autowired
  private JWTService jwtService;

  @Operation(summary = "Connexion utilisateur", description = "Connexion utilisateur")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Utilisateur connecté",
          content = {@Content(mediaType = "application/json",
          array = @ArraySchema(schema = @Schema(implementation = UserAuthentication.class))) }),
      @ApiResponse(responseCode = "403", description = "Session invalide"),
      @ApiResponse(responseCode = "400", description = "le login et le mot de passe sont obligatoires")
  })
  @PostMapping("/login")
  public ResponseEntity<UserAuthentication> login(@RequestBody @Valid UserCredentials credentials, HttpServletRequest request, HttpServletResponse response) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(credentials.login(), credentials.password());
    Authentication authentication = authenticationManager.authenticate(authenticationToken);
    
    String jwt = jwtService.createToken(authentication);
    return new ResponseEntity<UserAuthentication>(
        new UserAuthentication(authentication.getName(), jwt),
        HttpStatus.OK);
  }
}
