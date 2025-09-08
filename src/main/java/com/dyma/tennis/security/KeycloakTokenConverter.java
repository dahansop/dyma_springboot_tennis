package com.dyma.tennis.security;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

@Component
public class KeycloakTokenConverter implements Converter<Jwt, JwtAuthenticationToken> {

    @Value("${jwt.auth.client-id}")
    private String clientId;

    @Value("${jwt.auth.principal-attribute}")
    private String principalAttribute;

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    public JwtAuthenticationToken convert(Jwt jwt) {
        Collection<String> roles = Optional.ofNullable(jwt.getClaimAsMap("resource_access"))
                .map(map -> map.get(clientId))
                .map(resource -> (Collection<String>) ((Map<String, Object>) resource).get("roles"))
                .orElse(Collections.emptyList());

        Set<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                roles.stream().map(SimpleGrantedAuthority::new)
        ).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, principalAttribute);
    }
}