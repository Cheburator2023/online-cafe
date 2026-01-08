package ru.otus.cafe.order.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.otus.cafe.order.client.MenuServiceClient;
import ru.otus.cafe.order.dto.CreateOrderRequest;
import ru.otus.cafe.order.dto.MenuItemInfoDto;
import ru.otus.cafe.order.dto.OrderItemRequest;
import ru.otus.cafe.order.model.Order;
import ru.otus.cafe.order.model.OrderItem;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderFactory {

    @Qualifier("menuServiceClientFallback")
    private final MenuServiceClient menuServiceClient;

    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request.userId(), request.specialInstructions());

        List<Long> menuItemIds = request.items().stream()
                .map(OrderItemRequest::menuItemId)
                .toList();

        List<MenuItemInfoDto> menuItems = menuServiceClient.getMenuItemsBatch(menuItemIds);

        Map<Long, MenuItemInfoDto> menuItemMap = menuItems.stream()
                .collect(Collectors.toMap(MenuItemInfoDto::id, item -> item));

        request.items().forEach(item -> {
            MenuItemInfoDto menuItem = menuItemMap.get(item.menuItemId());
            if (menuItem == null) {
                throw new IllegalArgumentException("Menu item not found: " + item.menuItemId());
            }
            OrderItem orderItem = new OrderItem(
                    item.menuItemId(),
                    menuItem.name(),
                    item.quantity(),
                    menuItem.price()
            );
            order.addItem(orderItem);
        });

        return order;
    }
}
