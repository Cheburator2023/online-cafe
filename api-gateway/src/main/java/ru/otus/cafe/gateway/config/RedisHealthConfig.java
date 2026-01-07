package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * Конфигурация Health Indicator для Redis.
 * Обеспечивает корректную проверку здоровья Redis с правильной аутентификацией.
 */
@Configuration
@Profile({"!test", "default", "docker"})
public class RedisHealthConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisHealthConfig.class);

    @Bean
    @Primary
    public ReactiveHealthIndicator redisHealthIndicator(ReactiveRedisConnectionFactory connectionFactory) {
        return () -> {
            logger.debug("Checking Redis health...");
            return connectionFactory.getReactiveConnection()
                    .ping()
                    .timeout(Duration.ofSeconds(3))
                    .map(result -> {
                        logger.debug("Redis health check passed with response: {}", result);
                        return Health.up()
                                .withDetail("response", result)
                                .withDetail("connection", "established")
                                .build();
                    })
                    .onErrorResume(ex -> {
                        logger.warn("Redis health check failed: {}", ex.getMessage());
                        return Mono.just(Health.down()
                                .withDetail("error", ex.getMessage())
                                .withDetail("connection", "failed")
                                .build());
                    });
        };
    }

    @Bean
    @Profile("test")
    public ReactiveHealthIndicator testRedisHealthIndicator() {
        return () -> Mono.just(Health.up()
                .withDetail("mode", "test")
                .withDetail("message", "Redis health check is disabled in test mode")
                .build());
    }

    /**
     * Создает ReactiveRedisTemplate для работы с Redis.
     */
    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveStringRedisTemplate(connectionFactory);
    }

    /**
     * Дополнительный health indicator, который проверяет возможность выполнения команд.
     */
    @Bean
    public ReactiveHealthIndicator redisCommandHealthIndicator(ReactiveStringRedisTemplate redisTemplate) {
        return () -> redisTemplate.opsForValue().set("health-check", "test", Duration.ofSeconds(10))
                .then(redisTemplate.opsForValue().get("health-check"))
                .map(value -> {
                    if ("test".equals(value)) {
                        return Health.up()
                                .withDetail("command", "successful")
                                .withDetail("operation", "read-write")
                                .build();
                    } else {
                        return Health.down()
                                .withDetail("command", "unexpected_value")
                                .withDetail("expected", "test")
                                .withDetail("actual", value)
                                .build();
                    }
                })
                .onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail("error", ex.getMessage())
                        .withDetail("command", "failed")
                        .build()))
                .timeout(Duration.ofSeconds(5))
                .onErrorResume(ex -> Mono.just(Health.down()
                        .withDetail("error", "timeout")
                        .withDetail("message", "Redis command execution timed out")
                        .build()));
    }
}