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
