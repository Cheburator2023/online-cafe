package ru.otus.cafe.gateway.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.otus.cafe.common.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {

        logger.error("Unhandled exception occurred for request to {}: {}",
                exchange.getRequest().getPath(), ex.getMessage(), ex);

        ApiResponse<Void> response = ApiResponse.error(
                "An unexpected error occurred. Please try again later.");

        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleIllegalArgumentException(
            IllegalArgumentException ex, ServerWebExchange exchange) {

        logger.warn("Invalid argument for request to {}: {}",
                exchange.getRequest().getPath(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(ex.getMessage());

        return Mono.just(ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response));
    }
}