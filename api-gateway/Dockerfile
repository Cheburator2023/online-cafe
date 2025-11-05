FROM openjdk:21-jdk-slim

WORKDIR /app

COPY src/api-gateway/build/libs/api-gateway-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]