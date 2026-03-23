package com.arena.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import java.util.stream.Collectors;

import java.util.*;

@Configuration
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    // ================= SECURITY FILTER =================
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                .cors(cors -> cors.configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true);
                    corsConfig.setMaxAge(3600L);
                    return corsConfig;
                }))

                .authorizeExchange(exchanges -> exchanges

                        // ✅ Public endpoints
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .pathMatchers("/actuator/health/**").permitAll()
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/problems/public/**").permitAll()
                        .pathMatchers("/api/v1/leaderboard/**").permitAll()
                        .pathMatchers("/ws/**").permitAll()

                        // 🔒 Protected endpoints
                        .pathMatchers("/api/v1/users/**").authenticated()
                        .pathMatchers("/api/v1/rooms/**").authenticated()
                        .pathMatchers("/api/v1/battles/**").authenticated()
                        .pathMatchers("/api/v1/submissions/**").authenticated()
                        .pathMatchers("/api/v1/ai/**").authenticated()

                        // 👑 Admin
                        .pathMatchers("/api/v1/admin/**").hasRole("ADMIN")

                        // 🔐 Default
                        .anyExchange().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    // ================= JWT DECODER (🔥 FIX) =================
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {

        NimbusReactiveJwtDecoder decoder = NimbusReactiveJwtDecoder
                .withJwkSetUri("http://localhost:8180/realms/coding-arena/protocol/openid-connect/certs")
                .build();

        // ✅ Validate issuer
        OAuth2TokenValidator<Jwt> withIssuer =
                JwtValidators.createDefaultWithIssuer("http://localhost:8180/realms/coding-arena");

        // 🔥 Validate audience (FIXES YOUR 401)
        OAuth2TokenValidator<Jwt> audienceValidator = jwt -> {
            if (jwt.getAudience().contains("arena-app")) {
                return OAuth2TokenValidatorResult.success();
            }
            return OAuth2TokenValidatorResult.failure(
                    new OAuth2Error("invalid_token", "Invalid audience", null)
            );
        };

        OAuth2TokenValidator<Jwt> validator =
                new DelegatingOAuth2TokenValidator<>(withIssuer, audienceValidator);

        decoder.setJwtValidator(validator);

        return decoder;
    }

    // ================= ROLE CONVERTER =================
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> jwtAuthenticationConverter() {

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());

        return new ReactiveJwtAuthenticationConverterAdapter(jwtConverter);
    }

    // ================= KEYCLOAK ROLE EXTRACTOR =================
    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

        @Override
        @SuppressWarnings("unchecked")
        public Collection<GrantedAuthority> convert(Jwt jwt) {

            final Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");

            if (realmAccess == null || realmAccess.isEmpty()) {
                return Collections.emptyList();
            }

            Collection<String> roles = (Collection<String>) realmAccess.get("roles");

            if (roles == null) {
                return Collections.emptyList();
            }

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }
    }
}