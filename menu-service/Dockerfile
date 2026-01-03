FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Копируем файлы сборки
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY menu-service/build.gradle.kts menu-service/
COPY common-lib/build.gradle.kts common-lib/

# Собираем приложение
RUN ./gradlew :menu-service:build -x test --no-daemon

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем собранный JAR из стадии сборки
COPY --from=builder /app/menu-service/build/libs/menu-service-*.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]