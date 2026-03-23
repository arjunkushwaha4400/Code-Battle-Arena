package com.arena.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class AuthenticationFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> ctx.getAuthentication())
                .filter(auth -> auth instanceof JwtAuthenticationToken)
                .cast(JwtAuthenticationToken.class)
                .flatMap(jwtAuth -> {

                    var jwt = jwtAuth.getToken();

                    String userId = jwt.getSubject();
                    String username = jwt.getClaimAsString("preferred_username");
                    String email = jwt.getClaimAsString("email");

                    // Build completely new writable headers
                    HttpHeaders newHeaders = new HttpHeaders();
                    newHeaders.putAll(exchange.getRequest().getHeaders());

                    // Remove any spoofed headers from client
                    newHeaders.remove("X-User-Id");
                    newHeaders.remove("X-Username");
                    newHeaders.remove("X-Email");
                    newHeaders.remove("X-Gateway-Source");

                    // Add gateway headers
                    newHeaders.set("X-User-Id", userId);
                    newHeaders.set("X-Username", username != null ? username : "");
                    newHeaders.set("X-Email", email != null ? email : "");
                    newHeaders.set("X-Gateway-Source", "api-gateway");

                    // Use ServerHttpRequestDecorator to avoid ReadOnlyHttpHeaders issue
                    ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public HttpHeaders getHeaders() {
                            return newHeaders;
                        }
                    };

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(decoratedRequest)
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}