plugins {
    java
    id("org.springframework.boot") version "3.2.0" apply false
    id("io.spring.dependency-management") version "1.1.4"
}

allprojects {
    group = "ru.otus.cafe"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    configurations {
        compileOnly {
            extendsFrom(configurations.annotationProcessor.get())
        }
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-actuator")
        implementation("io.micrometer:micrometer-registry-prometheus")
        implementation("io.micrometer:micrometer-observation")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            setExceptionFormat("full")
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        // Гарантируем генерацию XML отчетов
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }

        // Всегда запускать тесты
        outputs.upToDateWhen { false }

        // Настройки для CI
        systemProperty("java.awt.headless", "true")
        systemProperty("spring.profiles.active", "test")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }
}

project(":common-lib") {
    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("org.springframework:spring-tx")
        implementation("org.springframework.amqp:spring-rabbit")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
        archiveClassifier.set("")
    }
}

project(":discovery-service") {
    dependencies {
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server:4.1.0")
    }
}

project(":api-gateway") {
    dependencies {
        implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.0")
        implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.1.0")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
        implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
        implementation("io.github.resilience4j:resilience4j-spring-boot3:2.1.0")
        implementation("io.github.resilience4j:resilience4j-reactor:2.1.0")
        implementation(project(":common-lib"))

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

project(":user-service") {
    dependencies {
        implementation(project(":common-lib"))
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
        implementation("org.flywaydb:flyway-core")
        implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
        runtimeOnly("org.postgresql:postgresql")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

project(":menu-service") {
    dependencies {
        implementation(project(":common-lib"))
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.postgresql:postgresql")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

project(":order-service") {
    dependencies {
        implementation(project(":common-lib"))
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.postgresql:postgresql")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

project(":payment-service") {
    dependencies {
        implementation(project(":common-lib"))
        implementation("org.springframework.boot:spring-boot-starter-data-jpa")
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("org.springframework.boot:spring-boot-starter-web")
        implementation("org.springframework.boot:spring-boot-starter-amqp")
        implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
        implementation("org.flywaydb:flyway-core")
        runtimeOnly("org.postgresql:postgresql")
        compileOnly("org.projectlombok:lombok")
        annotationProcessor("org.projectlombok:lombok")

        // Для корректного определения main class
        implementation("org.springframework.boot:spring-boot-starter")
        // Тестовые зависимости
        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.springframework.boot:spring-boot-testcontainers")
        testImplementation("org.testcontainers:junit-jupiter")
        testImplementation("org.testcontainers:postgresql")
        testImplementation("org.testcontainers:rabbitmq")
    }
}