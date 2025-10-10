#!/bin/bash

echo "Setting up Online Cafe project structure..."

# Create directories
mkdir -p src/{api-gateway,common-lib,discovery-service,menu-service,order-service,payment-service,user-service}/src/main/java/ru/otus/cafe

# Create basic build files for each subproject
for service in api-gateway common-lib discovery-service menu-service order-service payment-service user-service; do
    cat > "src/$service/build.gradle.kts" << EOF
plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Dependencies will be inherited from root build.gradle.kts
}
EOF
done

echo "Project structure created successfully!"
echo "You can now run: ./gradlew build"