package com.arena.gateway.config;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimitConfig {

    @Bean
    public RateLimiterRegistry rateLimiterRegistry() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(1))
                .limitForPeriod(50)
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        return RateLimiterRegistry.of(config);
    }

    @Bean
    public RateLimiter defaultRateLimiter(RateLimiterRegistry registry) {
        return registry.rateLimiter("default");
    }

    @Bean
    public RateLimiter submissionRateLimiter(RateLimiterRegistry registry) {
        RateLimiterConfig submissionConfig = RateLimiterConfig.custom()
                .limitRefreshPeriod(Duration.ofSeconds(10))
                .limitForPeriod(5)
                .timeoutDuration(Duration.ofMillis(500))
                .build();

        return registry.rateLimiter("submission", submissionConfig);
    }
}