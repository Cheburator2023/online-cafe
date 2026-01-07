package ru.otus.cafe.gateway.fallback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.otus.cafe.common.dto.ApiResponse;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest(controllers = FallbackController.class)
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @ValueSource(strings = {"user", "menu", "order", "payment", "health", "actuator"})
    @DisplayName("Should return service unavailable for all services")
    void serviceFallback_ShouldReturnServiceUnavailable(String service) {
        webTestClient.get().uri("/fallback/" + service)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectStatus().isEqualTo(503)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertFalse(response.isSuccess());
                    assertNotNull(response.getMessage());
                });
    }

    @Test
    @DisplayName("Legacy user service fallback should work")
    void userServiceFallback_ShouldWork() {
        webTestClient.get().uri("/fallback/user-service")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectStatus().isEqualTo(503)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertFalse(response.isSuccess());
                });
    }

    @Test
    @DisplayName("Fallback for unknown service should return service unavailable")
    void serviceFallback_UnknownService_ShouldReturnServiceUnavailable() {
        webTestClient.get().uri("/fallback/unknown-service")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectStatus().isEqualTo(503)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertNotNull(response);
                    assertFalse(response.isSuccess());
                });
    }
}