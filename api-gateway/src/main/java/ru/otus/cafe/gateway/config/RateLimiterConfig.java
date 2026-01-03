package ru.otus.cafe.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import reactor.core.publisher.Mono;

@Configuration
@Profile("!test") // Отключаем в тестовом режиме
public class RateLimiterConfig {

    @Bean
    public KeyResolver userKeyResolver() {
        return exchange -> {
            if (exchange.getRequest().getRemoteAddress() != null &&
                    exchange.getRequest().getRemoteAddress().getAddress() != null) {
                return Mono.just(
                        exchange.getRequest()
                                .getRemoteAddress()
                                .getAddress()
                                .getHostAddress()
                );
            }
            return Mono.just("unknown");
        };
    }
}