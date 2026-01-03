package ru.otus.cafe.gateway.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.cafe.common.dto.ApiResponse;

import java.util.Map;

@RestController
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    private static final Map<String, String> SERVICE_NAMES = Map.of(
            "user", "User",
            "menu", "Menu",
            "order", "Order",
            "payment", "Payment"
    );

    @GetMapping("/fallback/{service}")
    public ResponseEntity<ApiResponse<Void>> serviceFallback(@PathVariable String service) {
        String serviceName = SERVICE_NAMES.getOrDefault(service, service);
        String message = String.format("%s service is temporarily unavailable. Please try again later.", serviceName);

        logger.warn("Fallback triggered for service: {}", service);

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(message));
    }

    // Старые методы для обратной совместимости
    @GetMapping("/fallback/user-service")
    public ResponseEntity<ApiResponse<Void>> userServiceFallback() {
        return serviceFallback("user");
    }

    @GetMapping("/fallback/menu-service")
    public ResponseEntity<ApiResponse<Void>> menuServiceFallback() {
        return serviceFallback("menu");
    }

    @GetMapping("/fallback/order-service")
    public ResponseEntity<ApiResponse<Void>> orderServiceFallback() {
        return serviceFallback("order");
    }

    @GetMapping("/fallback/payment-service")
    public ResponseEntity<ApiResponse<Void>> paymentServiceFallback() {
        return serviceFallback("payment");
    }
}