FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY menu-service/build/libs/menu-service-*.jar app.jar

EXPOSE 8001

ENTRYPOINT ["java", "-jar", "app.jar"]