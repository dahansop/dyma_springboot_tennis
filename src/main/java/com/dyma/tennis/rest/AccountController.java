package com.dyma.tennis.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dyma.tennis.model.UserCredentials;

import io.swagger.v3.oas.annotations.Operation;
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
  private AuthenticationManagerBuilder authenticationManagerBuilder;

  private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

  private final SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler();

  @Operation(summary = "Connexion utilisateur", description = "Connexion utilisateur")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Utilisateur connecté"),
      @ApiResponse(responseCode = "403", description = "Session invalide"),
      @ApiResponse(responseCode = "400", description = "le login et le mot de passe sont obligatoires")
  })
  @PostMapping("/login")
  public void login(@RequestBody @Valid UserCredentials credentials, HttpServletRequest request, HttpServletResponse response) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(credentials.login(), credentials.password());
    Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    SecurityContext securityContext = SecurityContextHolder.getContext();
    securityContext.setAuthentication(authentication);
    securityContextRepository.saveContext(securityContext, request, response);
  }

  @Operation(summary = "Déconnexion de l'utilisateur", description = "Déconnexion de l'utilisateur")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Utilisateur déconnecté"),
      @ApiResponse(responseCode = "403", description = "L'utilisateur n'est pas connecté")
  })
  @GetMapping("/logout")
  public void logout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
    securityContextLogoutHandler.logout(request, response, authentication);
  }
}
