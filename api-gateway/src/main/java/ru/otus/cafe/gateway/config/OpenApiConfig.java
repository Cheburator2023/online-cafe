package ru.otus.cafe.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("!test") // Отключаем в тестах
public class OpenApiConfig {

    @Value("${gateway.url:http://localhost:8080}")
    private String gatewayUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server()
                                .url(gatewayUrl)
                                .description("API Gateway")
                ))
                .info(new Info()
                        .title("Online Cafe API Gateway")
                        .version("1.0")
                        .description("Aggregated API Documentation for Online Cafe Microservices")
                        .contact(new Contact()
                                .name("Online Cafe Team")
                                .email("support@online-cafe.ru"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}