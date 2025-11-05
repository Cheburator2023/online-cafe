FROM openjdk:21-jdk-slim

WORKDIR /app

COPY src/payment-service/build/libs/payment-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8003

ENTRYPOINT ["java", "-jar", "app.jar"]