FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY order-service/build/libs/order-service-*.jar app.jar

EXPOSE 8002

ENTRYPOINT ["java", "-jar", "app.jar"]