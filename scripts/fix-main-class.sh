#!/bin/bash

echo "Fixing main class issues..."

# Проверяем структуру API Gateway
echo "=== Checking API Gateway Structure ==="
find src/api-gateway -name "*.java" -type f
echo ""

# Проверяем наличие главного класса
if [ -f "src/api-gateway/src/main/java/ru/otus/cafe/gateway/ApiGatewayApplication.java" ]; then
    echo "✓ ApiGatewayApplication.java found"
else
    echo "✗ ApiGatewayApplication.java NOT found - creating it..."
    mkdir -p src/api-gateway/src/main/java/ru/otus/cafe/gateway/
    cat > src/api-gateway/src/main/java/ru/otus/cafe/gateway/ApiGatewayApplication.java << 'EOF'
package ru.otus.cafe.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
EOF
fi

# Проверяем build.gradle.kts
echo ""
echo "=== Checking build.gradle.kts ==="
if grep -q "mainClass" src/api-gateway/build.gradle.kts; then
    echo "✓ mainClass configured in build.gradle.kts"
else
    echo "✗ mainClass NOT configured - updating build.gradle.kts..."
    cat > src/api-gateway/build.gradle.kts << 'EOF'
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("application")
}

application {
    mainClass.set("ru.otus.cafe.gateway.ApiGatewayApplication")
}

springBoot {
    mainClass.set("ru.otus.cafe.gateway.ApiGatewayApplication")
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-gateway:4.1.0")
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-reactor-resilience4j:3.1.0")
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client:4.1.0")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.1.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.1.0")
    implementation(project(":common-lib"))
}
EOF
fi

echo ""
echo "Fix completed!"