plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

group = "ru.otus.cafe"
version = "0.0.1-SNAPSHOT"

springBoot {
    mainClass.set("ru.otus.cafe.gateway.ApiGatewayApplication")
}

// Явно указываем mainClass для bootJar
tasks.named("bootJar") {
    manifest {
        attributes("Start-Class" to "ru.otus.cafe.gateway.ApiGatewayApplication")
    }
}

tasks.named("jar") {
    enabled = true
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.0")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.1.0")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.1.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.1.0")
    implementation(project(":common-lib"))

    // Для корректного определения main class
    implementation("org.springframework.boot:spring-boot-starter")
    // Тестовые зависимости
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
}