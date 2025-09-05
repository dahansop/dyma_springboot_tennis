package com.dyma.tennis.security;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class JWTService {

  @Value("${jwt.token-validity-in-seconds}")
  private long tokenValidityInSeconds;
  
  @Autowired
  private final JwtEncoder jwtEncoder;
  
  public JWTService(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }
  
  /**
   * Génère le token JWT
   * @param authentication
   * @return token JWT encodé
   */
  public String createToken(Authentication authentication) {
    // génère la liste des roles de l'utilisateur connecté
    List<String> roles = authentication.getAuthorities().stream()
    .map(GrantedAuthority::getAuthority)
    .toList();
    
    Instant now = Instant.now();
    
    JwtClaimsSet claims = JwtClaimsSet.builder()
        .subject(authentication.getName())
        .issuedAt(now)
        .expiresAt(now.plus(tokenValidityInSeconds, ChronoUnit.SECONDS))
        .claim(SecurityUtils.AUTHORITIES_CLAIM_KEY, roles)
        .build();

    // signe le token
    JwsHeader jwsHeader = JwsHeader.with(SecurityUtils.JWT_ALGORITHM).build();
    
    // génération du token
    return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }
}
