package ru.otus.cafe.gateway.config;

import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация здоровья для API Gateway.
 * Отключает ненужные health indicators для RabbitMQ.
 */
@Configuration
@Profile("!test")
public class HealthConfig {

    /**
     * Создает фиктивный HealthIndicator для RabbitMQ, который всегда возвращает UP.
     * Это необходимо, чтобы избежать ошибок при проверке здоровья RabbitMQ в API Gateway,
     * который не использует RabbitMQ напрямую.
     */
    @Bean
    public HealthContributor rabbitHealthIndicator() {
        return (HealthIndicator) () -> org.springframework.boot.actuate.health.Health.up()
                .withDetail("message", "RabbitMQ is not used by API Gateway")
                .withDetail("status", "not_configured")
                .build();
    }
}