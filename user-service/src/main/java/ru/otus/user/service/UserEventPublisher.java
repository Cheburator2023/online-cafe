package ru.otus.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.otus.user.event.UserCreatedEvent;
import ru.otus.user.event.UserUpdatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventPublisher {
    private final RabbitTemplate rabbitTemplate;

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend("user.created", event);
            log.info("Sent UserCreatedEvent to RabbitMQ for user ID: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to send UserCreatedEvent to RabbitMQ for user ID: {}", event.userId(), e);
        }
    }

    @EventListener
    public void handleUserUpdatedEvent(UserUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend("user.updated", event);
            log.info("Sent UserUpdatedEvent to RabbitMQ for user ID: {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to send UserUpdatedEvent to RabbitMQ for user ID: {}", event.userId(), e);
        }
    }
}