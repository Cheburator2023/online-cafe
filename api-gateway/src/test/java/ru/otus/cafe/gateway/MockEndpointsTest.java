package ru.otus.cafe.gateway;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@Import(ApiGatewayApplication.TestWebFluxConfig.class)
@ActiveProfiles("test")
class MockEndpointsTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Actuator health endpoint should return UP status")
    void actuatorHealthEndpoint_ShouldReturnUpStatus() {
        webTestClient.get().uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }

    @Test
    @DisplayName("Actuator info endpoint should return application info")
    void actuatorInfoEndpoint_ShouldReturnApplicationInfo() {
        webTestClient.get().uri("/actuator/info")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.app.name").isEqualTo("api-gateway-test");
    }
}