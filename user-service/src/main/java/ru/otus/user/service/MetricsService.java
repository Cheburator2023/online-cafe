package ru.otus.user.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final MeterRegistry meterRegistry;

    public void incrementCounter(String method, String statusCode) {
        Counter.builder("user_api_calls")
                .tag("method", method)
                .tag("status_code", statusCode)
                .register(meterRegistry)
                .increment();
    }

    public void incrementErrorCounter(String exceptionName) {
        Counter.builder("user_api_errors")
                .tag("exception", exceptionName)
                .register(meterRegistry)
                .increment();
    }
}