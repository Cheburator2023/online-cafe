package ru.otus.cafe.gateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.cafe.common.dto.ApiResponse;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user-service")
    public ResponseEntity<ApiResponse<Void>> userServiceFallback() {
        return ResponseEntity.status(503)
                .body(ApiResponse.error("User service is temporarily unavailable"));
    }

    @GetMapping("/fallback/menu-service")
    public ResponseEntity<ApiResponse<Void>> menuServiceFallback() {
        return ResponseEntity.status(503)
                .body(ApiResponse.error("Menu service is temporarily unavailable"));
    }

    @GetMapping("/fallback/order-service")
    public ResponseEntity<ApiResponse<Void>> orderServiceFallback() {
        return ResponseEntity.status(503)
                .body(ApiResponse.error("Order service is temporarily unavailable"));
    }

    @GetMapping("/fallback/payment-service")
    public ResponseEntity<ApiResponse<Void>> paymentServiceFallback() {
        return ResponseEntity.status(503)
                .body(ApiResponse.error("Payment service is temporarily unavailable"));
    }
}