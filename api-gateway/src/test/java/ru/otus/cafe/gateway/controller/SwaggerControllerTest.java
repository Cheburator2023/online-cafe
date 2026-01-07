package ru.otus.cafe.gateway.controller;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest(controllers = SwaggerController.class)
class SwaggerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @Disabled
    @DisplayName("Root endpoint should redirect to Swagger UI")
    void redirectToSwagger_ShouldRedirectToSwaggerUi() {
        webTestClient.get().uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/swagger-ui.html");
    }

    @Test
    @Disabled
    @DisplayName("/swagger endpoint should redirect to Swagger UI")
    void swagger_ShouldRedirectToSwaggerUi() {
        webTestClient.get().uri("/swagger")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals("Location", "/swagger-ui.html");
    }
}