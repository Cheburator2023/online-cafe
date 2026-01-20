package ru.otus.cafe.gateway.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.cafe.common.dto.ApiResponse;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    private static final Map<String, String> SERVICE_NAMES = Map.of(
            "user", "User",
            "menu", "Menu",
            "order", "Order",
            "payment", "Payment",
            "health", "Health",
            "actuator", "Actuator"
    );

    @RequestMapping(value = "/{service}", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE,
            RequestMethod.PATCH})
    public ResponseEntity<ApiResponse<Void>> serviceFallback(@PathVariable String service) {
        String serviceName = SERVICE_NAMES.getOrDefault(service, service);
        String message = String.format("%s service is temporarily unavailable. Please try again later.", serviceName);

        logger.warn("Fallback triggered for service: {} with proper HTTP method handling", service);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(HttpStatus.SERVICE_UNAVAILABLE.toString(), message));
    }

    @RequestMapping(value = "/user-service", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ApiResponse<Void>> userServiceFallback() {
        return serviceFallback("user");
    }

    @RequestMapping(value = "/menu-service", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ApiResponse<Void>> menuServiceFallback() {
        return serviceFallback("menu");
    }

    @RequestMapping(value = "/order-service", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ApiResponse<Void>> orderServiceFallback() {
        return serviceFallback("order");
    }

    @RequestMapping(value = "/payment-service", method = {RequestMethod.GET, RequestMethod.POST,
            RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<ApiResponse<Void>> paymentServiceFallback() {
        return serviceFallback("payment");
    }

    @RequestMapping(value = "/actuator", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ApiResponse<Void>> actuatorFallback() {
        return serviceFallback("actuator");
    }

    @RequestMapping(value = "/health", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<ApiResponse<Void>> healthFallback() {
        return serviceFallback("health");
    }
}