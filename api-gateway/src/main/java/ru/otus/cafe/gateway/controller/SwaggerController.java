package ru.otus.cafe.gateway.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SwaggerController {

    private static final Logger logger = LoggerFactory.getLogger(SwaggerController.class);

    @GetMapping("/")
    public String redirectToSwagger(ServerHttpRequest request) {
        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
        logger.info("Redirecting to Swagger UI from IP: {}", clientIp);
        return "redirect:/swagger-ui.html";
    }

    @GetMapping("/swagger")
    public String swagger(ServerHttpRequest request) {
        String clientIp = request.getRemoteAddress() != null
                ? request.getRemoteAddress().getAddress().getHostAddress()
                : "unknown";
        logger.debug("Swagger endpoint accessed from IP: {}", clientIp);
        return "redirect:/swagger-ui.html";
    }
}