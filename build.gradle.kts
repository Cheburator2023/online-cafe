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

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
            setExceptionFormat("full")
            showExceptions = true
            showCauses = true
            showStackTraces = true
        }

        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }

        outputs.upToDateWhen { false }
        systemProperty("java.awt.headless", "true")
        systemProperty("spring.profiles.active", "test")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-parameters"))
    }
}

project(":common-lib") {
    apply(plugin = "org.springframework.boot")

    tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
        enabled = false
    }

    tasks.getByName<Jar>("jar") {
        enabled = true
        archiveClassifier.set("")
    }

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-validation")
        implementation("com.fasterxml.jackson.core:jackson-databind")
        implementation("org.springframework:spring-tx")
        implementation("org.springframework.amqp:spring-rabbit")
        implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
        implementation("org.springframework.boot:spring-boot-starter")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
    }
}