# Используем образ с JDK для сборки
FROM eclipse-temurin:21-jdk-alpine AS build

# Устанавливаем рабочую директорию
WORKDIR /workspace

# Копируем исходный код
COPY payment-service/build.gradle.kts .
COPY payment-service/src ./src
COPY common-lib ../common-lib

# Создаем структуру проекта
RUN mkdir -p ../common-lib/src

# Собираем проект
RUN gradle build -x test --no-daemon

# Финальный образ
FROM eclipse-temurin:21-jre-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR
COPY --from=build /workspace/build/libs/payment-service-*.jar app.jar

# Открываем порт
EXPOSE 8003

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]