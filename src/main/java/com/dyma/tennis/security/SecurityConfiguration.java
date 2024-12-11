package com.dyma.tennis.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Autowired
  private DymaUserDetailsService dymaUserDetailsService;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
    authenticationProvider.setUserDetailsService(dymaUserDetailsService);
    authenticationProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authenticationProvider);
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable()) // car sinon considère le post du login comme dangereux (changement d'état possible)
        // ajout d'une sécurité pour reExeception :fuser tout téléchargement de l'extérieur. on ajoute le data pour le simages de swagger
        // on permet le téléchargement des styles en ligne
        // interdiction de mettre l'application dans une frame
        // plein ecran uniquement de l'appli ; caméra, microphone et geoloclisation interdite
        .headers(headers -> 
            headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self' data:; style-src 'self' 'unsafe-line';"))
            .frameOptions(frameOptionsConfig -> frameOptionsConfig.deny())
            .permissionsPolicyHeader(permissionsPolicyConfig -> permissionsPolicyConfig.policy("fullscreen=(self), geolocalisation=(), microphone=(), camera=()")))
        .authorizeHttpRequests(authorizations ->  
          authorizations
          //.requestMatchers(HttpMethod.POST, "/players/**").hasAuthority("ROLE_USER") fonctionne aussi car l'admin a le droit role_user
            .requestMatchers(HttpMethod.GET, "/players/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.POST, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/accounts/login").permitAll()
            .requestMatchers("/healthcheck/**").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
  }
}
