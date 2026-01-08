package ru.otus.cafe.order.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import ru.otus.cafe.order.client.MenuServiceClient;
import ru.otus.cafe.order.dto.MenuItemInfoDto;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig {

    @MockBean
    private MenuServiceClient menuServiceClient;

    @Bean
    public MenuServiceClient mockMenuServiceClient() {
        when(menuServiceClient.getMenuItemsBatch(anyList()))
                .thenAnswer(invocation -> {
                    List<Long> ids = invocation.getArgument(0);
                    return ids.stream()
                            .map(id -> new MenuItemInfoDto(id, "Test Item " + id, BigDecimal.valueOf(10.0)))
                            .toList();
                });
        return menuServiceClient;
    }
}