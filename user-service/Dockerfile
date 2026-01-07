FROM eclipse-temurin:21-jdk-alpine

RUN apk add --no-cache curl

WORKDIR /app

COPY user-service/build/libs/user-service-*.jar app.jar

EXPOSE 8000

ENTRYPOINT ["java", "-jar", "app.jar"]