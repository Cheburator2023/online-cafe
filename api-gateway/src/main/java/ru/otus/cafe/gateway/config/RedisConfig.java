package ru.otus.cafe.gateway.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.protocol.ProtocolVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Конфигурация Redis для API Gateway.
 * Решает проблемы аутентификации с Redis 7+ и протоколом RESP3.
 */
@Configuration
@Profile({"!test", "default", "docker"})
public class RedisConfig {

    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    @Primary
    public ReactiveRedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        logger.info("Configuring Redis connection factory with host: {}, port: {}",
                redisProperties.getHost(), redisProperties.getPort());

        // Создаем конфигурацию Redis
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());

        // Настраиваем аутентификацию
        if (redisProperties.getPassword() != null && !redisProperties.getPassword().isEmpty()) {
            config.setPassword(RedisPassword.of(redisProperties.getPassword()));
            logger.info("Redis authentication enabled");
        } else {
            logger.warn("Redis authentication is not configured");
        }

        if (redisProperties.getUsername() != null && !redisProperties.getUsername().isEmpty()) {
            config.setUsername(redisProperties.getUsername());
        } else {
            config.setUsername("default");
            logger.info("Using default Redis username: default");
        }

        config.setDatabase(redisProperties.getDatabase());

        // Конфигурация клиента Lettuce
        LettuceClientConfiguration.LettuceClientConfigurationBuilder clientConfigBuilder =
                LettuceClientConfiguration.builder();

        // Явно устанавливаем RESP2 протокол для совместимости
        clientConfigBuilder.clientOptions(
                ClientOptions.builder()
                        .protocolVersion(ProtocolVersion.RESP2)  // Используем RESP2 для совместимости
                        .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                        .autoReconnect(true)
                        .build()
        );

        // Настраиваем пул соединений
        if (redisProperties.getLettuce() != null && redisProperties.getLettuce().getPool() != null) {
            RedisProperties.Pool poolProps = redisProperties.getLettuce().getPool();
            clientConfigBuilder = LettucePoolingClientConfiguration.builder()
                    .poolConfig(createLettucePoolConfig(poolProps));
        }

        // Настраиваем таймауты
        if (redisProperties.getTimeout() != null) {
            clientConfigBuilder.commandTimeout(redisProperties.getTimeout());
        }

        if (redisProperties.getConnectTimeout() != null) {
            clientConfigBuilder.shutdownTimeout(redisProperties.getConnectTimeout());
        }

        LettuceClientConfiguration clientConfig = clientConfigBuilder.build();

        // Создаем фабрику соединений
        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
        factory.setValidateConnection(true);
        factory.afterPropertiesSet();

        logger.info("Redis connection factory configured successfully");
        return factory;
    }

    @Bean
    @Profile("test")
    public ReactiveRedisConnectionFactory testRedisConnectionFactory() {
        logger.info("Creating test Redis connection factory");

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName("localhost");
        config.setPort(6379);
        config.setUsername("default");

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientOptions(
                        ClientOptions.builder()
                                .protocolVersion(ProtocolVersion.RESP2)
                                .build()
                )
                .commandTimeout(Duration.ofSeconds(2))
                .build();

        LettuceConnectionFactory factory = new LettuceConnectionFactory(config, clientConfig);
        factory.afterPropertiesSet();
        return factory;
    }

    private org.apache.commons.pool2.impl.GenericObjectPoolConfig<?> createLettucePoolConfig(
            RedisProperties.Pool poolProps) {

        org.apache.commons.pool2.impl.GenericObjectPoolConfig<?> poolConfig =
                new org.apache.commons.pool2.impl.GenericObjectPoolConfig<>();

        poolConfig.setMaxTotal(poolProps.getMaxActive());
        poolConfig.setMaxIdle(poolProps.getMaxIdle());
        poolConfig.setMinIdle(poolProps.getMinIdle());

        if (poolProps.getMaxWait() != null) {
            poolConfig.setMaxWait(poolProps.getMaxWait());
        }

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));

        return poolConfig;
    }

    /**
     * Создает ReactiveStringRedisTemplate для работы со строками.
     */
    @Bean
    public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveStringRedisTemplate(connectionFactory);
    }

    /**
     * Создает общий ReactiveRedisTemplate для различных типов данных.
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {

        StringRedisSerializer keySerializer = new StringRedisSerializer();
        RedisSerializer<Object> valueSerializer = RedisSerializer.json();

        RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
                RedisSerializationContext.newSerializationContext(keySerializer);

        RedisSerializationContext<String, Object> context = builder
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}