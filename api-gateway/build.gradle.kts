plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "ru.otus.cafe"
version = "0.0.1-SNAPSHOT"

springBoot {
    mainClass.set("ru.otus.cafe.gateway.ApiGatewayApplication")
}

dependencies {
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")

    // Caffeine cache для LoadBalancer (рекомендуется для production)
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("org.springframework:spring-context-support")

    // Reactive Redis с поддержкой пулинга
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.lettuce:lettuce-core:6.3.2.RELEASE")
    implementation("org.apache.commons:commons-pool2:2.12.0")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-spring-boot3")
    implementation("io.github.resilience4j:resilience4j-reactor")

    // Common library (исключаем ненужные зависимости)
    implementation(project(":common-lib")) {
        exclude(group = "org.springframework.amqp", module = "spring-rabbit")
    }

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

    // Для работы с JSON в тестах
    testImplementation("com.jayway.jsonpath:json-path:2.8.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
        mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.0")
    }
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