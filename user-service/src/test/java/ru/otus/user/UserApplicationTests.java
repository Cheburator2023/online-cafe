package ru.otus.user;

import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
class UserApplicationTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("user_db")
            .withUsername("test")
            .withPassword("test");

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public DataSource dataSource() {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setJdbcUrl(postgres.getJdbcUrl());
            dataSource.setUsername(postgres.getUsername());
            dataSource.setPassword(postgres.getPassword());
            dataSource.setDriverClassName(postgres.getDriverClassName());
            return dataSource;
        }
    }

    @Test
    void contextLoads(ApplicationContext context) {
        assertNotNull(context);
    }

    @Test
    @Disabled
    void mainMethodStartsApplication() {
        UserApplication.main(new String[]{});
    }
}