#!/bin/bash

echo "Cleaning up project structure..."

# Удаляем корневой source code если он существует
if [ -d "src/main/java" ]; then
    echo "Removing root source directory..."
    rm -rf src/main/java
    rm -rf src/main/resources
    rm -rf src/test
fi

# Удаляем корневой application.properties если существует
if [ -f "src/main/resources/application.properties" ]; then
    rm src/main/resources/application.properties
fi

# Создаем базовую структуру для подпроектов если не существует
mkdir -p src/{api-gateway,common-lib,discovery-service,menu-service,order-service,payment-service,user-service}/src/main/java/ru/otus/cafe

echo "Project cleanup completed!"
echo "You can now run: ./gradlew clean build"