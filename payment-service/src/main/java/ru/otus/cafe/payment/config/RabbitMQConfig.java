package ru.otus.cafe.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDER_CREATED_QUEUE = "order.created.queue";
    public static final String PAYMENT_PROCESSED_QUEUE = "payment.processed.queue";
    public static final String PAYMENT_EXCHANGE = "payment.exchange";

    @Bean
    public Queue orderCreatedQueue() {
        return QueueBuilder.durable(ORDER_CREATED_QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", ORDER_CREATED_QUEUE + ".dlq")
                .build();
    }

    @Bean
    public Queue orderCreatedDLQ() {
        return new Queue(ORDER_CREATED_QUEUE + ".dlq");
    }

    @Bean
    public Queue paymentProcessedQueue() {
        return QueueBuilder.durable(PAYMENT_PROCESSED_QUEUE).build();
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange(PAYMENT_EXCHANGE);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderCreatedQueue())
                .to(paymentExchange())
                .with("order.created");
    }

    @Bean
    public Binding paymentProcessedBinding() {
        return BindingBuilder.bind(paymentProcessedQueue())
                .to(paymentExchange())
                .with("payment.processed");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        rabbitTemplate.setChannelTransacted(true);
        return rabbitTemplate;
    }
}