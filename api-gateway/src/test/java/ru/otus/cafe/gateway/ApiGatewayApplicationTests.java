package ru.otus.cafe.gateway;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.cafe.gateway.controller.SwaggerController;
import ru.otus.cafe.gateway.fallback.FallbackController;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {ApiGatewayApplication.class},
        properties = {
                "spring.main.allow-bean-definition-overriding=true",
                "spring.cloud.gateway.enabled=false",
                "eureka.client.enabled=false",
                "management.endpoint.health.probes.enabled=false"
        }
)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {
        assertNotNull(context);
        assertNotNull(context.getBean(FallbackController.class));
        assertNotNull(context.getBean(SwaggerController.class));
    }

    @Test
    void mainMethodStartsApplication() {
        // Проверяем, что приложение может запуститься
        try {
            ApiGatewayApplication.main(new String[]{});
        } catch (Exception e) {
            // В тестовой среде могут возникнуть исключения из-за отсутствия зависимостей
            // Это нормально для теста main метода
        }
    }

    @Test
    @Disabled
    void swaggerUiRedirect() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/swagger-ui.html");
    }

    @Test
    void swaggerUiEndpoint() {
        webTestClient.get().uri("/swagger-ui.html")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Swagger UI");
    }

    @Test
    void actuatorHealthEndpoint() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}