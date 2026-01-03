package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test") // Отключаем в тестах
public class SwaggerConfig {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerConfig.class);

    private static final List<String> SERVICE_IDS = List.of(
            "user-service",
            "menu-service",
            "order-service",
            "payment-service"
    );

    @Bean
    public List<GroupedOpenApi> groupedOpenApis() {
        return SERVICE_IDS.stream()
                .map(serviceId -> {
                    String serviceName = serviceId.replace("-service", "");
                    GroupedOpenApi group = GroupedOpenApi.builder()
                            .group(serviceId)
                            .pathsToMatch("/api/" + serviceName + "/**")
                            .build();
                    logger.info("Created Swagger group for service: {}", serviceId);
                    return group;
                })
                .toList();
    }
}