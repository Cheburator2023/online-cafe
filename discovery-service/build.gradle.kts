plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

springBoot {
    mainClass.set("ru.otus.cafe.discovery.DiscoveryApplication")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
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