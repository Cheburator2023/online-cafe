package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Конфигурация для управления временем запуска и регистрацией сервисов.
 */
@Configuration
@Profile({"!test", "default", "docker"})
public class StartupConfig {

    private static final Logger logger = LoggerFactory.getLogger(StartupConfig.class);

    /**
     * Задержка перед началом регистрации в Eureka, чтобы убедиться,
     * что все зависимости готовы.
     */
    @Bean
    public ApplicationListener<ApplicationReadyEvent> startupListener() {
        return event -> {
            logger.info("API Gateway application is ready. Waiting for dependencies to stabilize...");

            try {
                // Даем время другим сервисам (особенно discovery-service) полностью запуститься
                Thread.sleep(10000); // 10 секунд задержки
                logger.info("Startup delay completed. Starting service registration...");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Startup delay was interrupted", e);
            }
        };
    }
}