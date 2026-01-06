FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY payment-service/build/libs/payment-service-*.jar app.jar

EXPOSE 8003

ENTRYPOINT ["java", "-jar", "app.jar"]