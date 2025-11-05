package ru.otus.cafe.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    // Убираем @Bean метод, так как зависимости Swagger несовместимы с WebFlux в Gateway
    // Вместо этого настраиваем документацию через свойства SpringDoc

    // Альтернативно, можно использовать SpringDoc для WebFlux
    // Но для API Gateway обычно документация не требуется
}