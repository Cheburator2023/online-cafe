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

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("ru.otus.cafe.gateway.ApiGatewayApplication")
    archiveFileName.set("api-gateway-${version}.jar")
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

dependencies {
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")

    // Reactive Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("io.github.resilience4j:resilience4j-reactor")

    // Common library
    implementation(project(":common-lib"))

    // WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // SpringDoc OpenAPI для WebFlux
    implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.3.0")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Тестирование
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.awaitility:awaitility:4.2.0")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showCauses = true
        showStackTraces = true
    }
}