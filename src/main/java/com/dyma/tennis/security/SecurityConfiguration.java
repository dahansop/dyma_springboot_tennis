package com.dyma.tennis.security;

import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  private final Logger LOGGER = LoggerFactory.getLogger(SecurityConfiguration.class);
  
  @Value("${jwt.base64-secret}")
  private String jwtSecret;
  
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
        // ajout d'une sécurité pour reExeception :fuser tout téléchargement de l'extérieur. on ajoute le data pour les images de swagger
        // on permet le téléchargement des styles en ligne
        // interdiction de mettre l'application dans une frame
        // plein ecran uniquement de l'appli ; caméra, microphone et geoloclisation interdite
        .headers(headers ->
            headers.contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self' data:; style-src 'self' 'unsafe-line';"))
            .frameOptions(frameOptionsConfig -> frameOptionsConfig.deny())
            .permissionsPolicyHeader(permissionsPolicyConfig -> permissionsPolicyConfig.policy("fullscreen=(self), geolocalisation=(), microphone=(), camera=()")))
        .authorizeHttpRequests(authorizations ->
          authorizations
            .requestMatchers("/swagger-ui/**").permitAll()
            .requestMatchers("/v3/api-docs/**").permitAll()
            .requestMatchers("/accounts/login").permitAll()
            .requestMatchers("/healthcheck/**").permitAll()
            .requestMatchers("/actuator/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.GET, "/players/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.POST, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/players/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.GET, "/tournaments/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
            .requestMatchers(HttpMethod.POST, "/tournaments/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.PUT, "/tournaments/**").hasAuthority("ROLE_ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/tournaments/**").hasAuthority("ROLE_ADMIN")
            .anyRequest().authenticated())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(oauth2 -> 
          oauth2.jwt(jwt -> 
            jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
          )
        );
    return http.build();
  }
  
  // JWT configuration
  @Bean
  public JwtEncoder jwtEncoder() {
    return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
  }
  
  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
        .withSecretKey(getSecretKey())
        .macAlgorithm(SecurityUtils.JWT_ALGORITHM)
        .build();
    return token -> {
      try {
        return jwtDecoder.decode(token);
      } catch(Exception e) {
        LOGGER.error("Could not decode JWT: {}", e.getMessage());
        throw e;
      }
    };
  }
  
  @Bean
  public JwtAuthenticationConverter jwtAuthenticationConverter() {
    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
      return jwt.getClaimAsStringList(SecurityUtils.AUTHORITIES_CLAIM_KEY)
        .stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
    });
    return jwtAuthenticationConverter;
  }
  
  private SecretKey getSecretKey() {
    byte[] keyBytes = Base64.from(jwtSecret).decode();
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtils.JWT_ALGORITHM.getName());
  }
}
