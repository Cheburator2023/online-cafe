package ru.otus.cafe.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile({"!test", "default", "docker"})
public class GatewayRoutesConfig {

    private static final Logger logger = LoggerFactory.getLogger(GatewayRoutesConfig.class);

    private static final List<String> SERVICES = List.of("user", "menu", "order", "payment");

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

        // Маршруты для сервисов
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
                            .preserveHostHeader()
                    )
                    .uri(uriPrefix + serviceId.toUpperCase()));
        }

        // Маршрут для Actuator
        routes.route("actuator", r -> r
                .path("/actuator/**")
                .filters(GatewayFilterSpec::preserveHostHeader)
                .uri(uriPrefix + "API-GATEWAY"));

        // Маршрут для Swagger UI
        routes.route("swagger-ui", r -> r
                .path("/swagger-ui.html", "/webjars/**", "/v3/api-docs/**", "/swagger-resources/**")
                .filters(GatewayFilterSpec::preserveHostHeader)
                .uri(uriPrefix + "API-GATEWAY"));

        // Маршрут для редиректа с корневого пути на Swagger UI
        routes.route("root-redirect", r -> r
                .path("/")
                .filters(f -> f.redirect(302, "/swagger-ui.html"))
                .uri("http://no-uri-required.com"));

        // Маршрут для редиректа с /swagger на Swagger UI
        routes.route("swagger-redirect", r -> r
                .path("/swagger")
                .filters(f -> f.redirect(302, "/swagger-ui.html"))
                .uri("http://no-uri-required.com"));

        return routes.build();
    }
}