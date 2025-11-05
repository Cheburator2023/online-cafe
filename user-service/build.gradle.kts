plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("java")
}

group = "ru.otus.cafe"
version = "0.0.1-SNAPSHOT"

springBoot {
    mainClass.set("ru.otus.user.UserApplication")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    mainClass.set("ru.otus.user.UserApplication")
    archiveFileName.set("user-service-${version}.jar")
}

tasks.named<Jar>("jar") {
    enabled = true
    archiveClassifier.set("")
}

dependencies {
    implementation(project(":common-lib"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    implementation("org.springframework.boot:spring-boot-starter")
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
}