# Используем образ с JDK для сборки
FROM eclipse-temurin:21-jdk-alpine AS build

# Устанавливаем рабочую директорию
WORKDIR /workspace

# Копируем файлы сборки
COPY ../gradle ./gradle
COPY ../gradlew .
COPY ../gradle.properties .
COPY ../settings.gradle.kts .
COPY ../build.gradle.kts .

# Копируем исходный код модулей
COPY common-lib ./common-lib
COPY payment-service ./payment-service

# Даем права на выполнение gradlew
RUN chmod +x gradlew

# Собираем проект (только нужные модули)
RUN ./gradlew :payment-service:build -x test --no-daemon

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR
COPY --from=build /workspace/payment-service/build/libs/*.jar app.jar

# Открываем порт
EXPOSE 8003

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]