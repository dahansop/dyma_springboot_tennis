package com.dyma.tennis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

/**
 * surcharge la sécurité pour l'ignoré pour le profil test
 */
@Configuration
@Profile("test")
public class ApplicationWithoutSecurity {

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web -> web.ignoring().anyRequest());
  }
}
