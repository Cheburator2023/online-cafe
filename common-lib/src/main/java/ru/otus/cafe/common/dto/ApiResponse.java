package ru.otus.cafe.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private ErrorResponse error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, null, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, LocalDateTime.now(), null);
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(false, error.getMessage(), null, LocalDateTime.now(), error);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        ErrorResponse error = new ErrorResponse(code, message);
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), error);
    }

    public static <T> ApiResponse<T> error(String code, String message, String details) {
        ErrorResponse error = new ErrorResponse(code, message, details);
        return new ApiResponse<>(false, message, null, LocalDateTime.now(), error);
    }

    public static <T> ApiResponse<T> error(String message) {
        return error("INTERNAL_ERROR", message);
    }
}