plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

springBoot {
    mainClass.set("ru.otus.cafe.discovery.DiscoveryApplication")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:4.1.0")
    // Для корректного определения main class
    implementation("org.springframework.boot:spring-boot-starter")
    // Тестовые зависимости
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
}