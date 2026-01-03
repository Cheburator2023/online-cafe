FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY build/libs/discovery-service-*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]