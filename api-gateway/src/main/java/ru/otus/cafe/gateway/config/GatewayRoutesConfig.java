package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"!test", "default", "docker"})
public class GatewayRoutesConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayRoutesConfig.class);

    private static final String[] SERVICES = {"user", "menu", "order", "payment"};

    @Bean
    @Profile({"!docker", "default"})
    public RouteLocator localRoutes(RouteLocatorBuilder builder) {
        logger.info("Configuring local routes for API Gateway");
        return buildRoutes(builder, "lb://");
    }

    @Bean
    @Profile("docker")
    public RouteLocator dockerRoutes(RouteLocatorBuilder builder) {
        logger.info("Configuring Docker routes for API Gateway");
        return buildRoutes(builder, "lb://");
    }

    private RouteLocator buildRoutes(RouteLocatorBuilder builder, String uriPrefix) {
        RouteLocatorBuilder.Builder routes = builder.routes();

        for (String service : SERVICES) {
            String serviceId = service + "-service";
            String apiPath = "/api/" + service + "/**";
            String fallbackUri = "forward:/fallback/" + service;

            routes.route(serviceId, r -> r
                    .path(apiPath)
                    .filters(f -> f
                            .circuitBreaker(config -> config
                                    .setName(serviceId)
                                    .setFallbackUri(fallbackUri))
                            .rewritePath("/api/" + service + "/(?<segment>.*)", "/${segment}")
                    )
                    .uri(uriPrefix + serviceId.toUpperCase()));
        }

        // Маршрут для Swagger UI
        routes.route("swagger-ui", r -> r
                .path("/swagger-ui.html", "/webjars/**", "/v3/api-docs/**")
                .uri("http://localhost:8080"));

        return routes.build();
    }
}