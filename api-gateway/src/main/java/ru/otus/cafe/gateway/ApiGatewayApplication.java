package ru.otus.cafe.gateway;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayApplication.class);

    private final Environment environment;

    public ApiGatewayApplication(Environment environment) {
        this.environment = environment;
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @PostConstruct
    public void init() {
        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles.length == 0) {
            logger.info("API Gateway application started successfully with default profile");
        } else {
            logger.info("API Gateway application started successfully with profiles: {}",
                    String.join(", ", activeProfiles));
        }
    }

    @Configuration
    @Profile("test")
    static class TestWebFluxConfig {

        @Bean
        public RouterFunction<ServerResponse> testRoutes() {
            return route(GET("/actuator/health"),
                    request -> ServerResponse.ok()
                            .bodyValue("{\"status\":\"UP\"}"))
                    .andRoute(GET("/actuator/info"),
                            request -> ServerResponse.ok()
                                    .bodyValue("{\"app\":{\"name\":\"api-gateway-test\"}}"))
                    .andRoute(GET("/swagger-ui.html"),
                            request -> ServerResponse.ok().bodyValue("Swagger UI"))
                    // Редирект с корневого пути на Swagger UI
                    .andRoute(GET("/"),
                            request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build())
                    // Редирект с /swagger на Swagger UI
                    .andRoute(GET("/swagger"),
                            request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui.html")).build());
        }
    }
}