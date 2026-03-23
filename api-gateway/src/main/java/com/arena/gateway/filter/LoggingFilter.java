package com.arena.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().value();
        String method = request.getMethod().name();

        log.info("[{}] Incoming request: {} {}", requestId, method, path);

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doFinally(signalType -> {
                    long duration = System.currentTimeMillis() - startTime;
                    int statusCode = exchange.getResponse().getStatusCode() != null
                            ? exchange.getResponse().getStatusCode().value()
                            : 0;

                    log.info("[{}] Response: {} {} - Status: {} - Duration: {}ms",
                            requestId, method, path, statusCode, duration);
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}