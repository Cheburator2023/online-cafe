package ru.otus.cafe.user.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {
    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                return Health.up().withDetail("database", "PostgreSQL").build();
            } else {
                return Health.down().withDetail("database", "Connection invalid").build();
            }
        } catch (Exception e) {
            return Health.down().withDetail("database", e.getMessage()).build();
        }
    }
}