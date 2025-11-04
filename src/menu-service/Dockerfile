FROM openjdk:21-jdk-slim

WORKDIR /app

COPY src/menu-service/build/libs/menu-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]