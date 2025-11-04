FROM openjdk:21-jdk-slim

WORKDIR /app

COPY src/order-service/build/libs/order-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "app.jar"]