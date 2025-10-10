package ru.otus.cafe.order.mapper;

import org.springframework.stereotype.Component;
import ru.otus.cafe.order.dto.OrderItemResponse;
import ru.otus.cafe.order.dto.OrderResponse;
import ru.otus.cafe.order.model.Order;
import ru.otus.cafe.order.model.OrderItem;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getSpecialInstructions(),
                itemResponses,
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getMenuItemId(),
                item.getMenuItemName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal()
        );
    }
}