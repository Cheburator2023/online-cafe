plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "ru.otus.cafe"
version = "0.0.1-SNAPSHOT"

springBoot {
    mainClass.set("ru.otus.cafe.payment.PaymentApplication")
}

dependencies {
    implementation(project(":common-lib"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.flywaydb:flyway-core")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.micrometer:micrometer-registry-prometheus")

    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("com.h2database:h2") // Добавлено для тестов
    testImplementation("org.awaitility:awaitility")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        setExceptionFormat("full")
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
    systemProperty("spring.profiles.active", "test")
}