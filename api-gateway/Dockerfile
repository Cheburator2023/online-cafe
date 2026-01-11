FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl iputils

WORKDIR /app

COPY api-gateway/build/libs/api-gateway-*.jar app.jar

EXPOSE 8080

# Явно задаем переменные окружения для Docker среды
ENV SPRING_PROFILES_ACTIVE=docker
ENV EUREKA_CLIENT_ENABLED=true
ENV EUREKA_INSTANCE_PREFER_IP_ADDRESS=true
ENV EUREKA_INSTANCE_HOSTNAME=api-gateway
ENV SERVER_PORT=8080
ENV MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics,prometheus
ENV LOGGING_LEVEL_RU_OTUS_CAFE_GATEWAY=INFO
ENV LOGGING_LEVEL_ORG_SPRINGFRAMEWORK_CLOUD_GATEWAY=INFO
ENV LOGGING_LEVEL_COM_NETFLIX_EUREKA=WARN
ENV REDIS_USERNAME=default

# Создаем не-root пользователя для безопасности
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Добавляем health check для Docker
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]