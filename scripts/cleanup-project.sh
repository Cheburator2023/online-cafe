#!/bin/bash

echo "Cleaning up project structure..."

# Удаляем корневой source code если он существует
if [ -d "src/main/java" ]; then
    echo "Removing root source directory..."
    rm -rf src/main/java
    rm -rf src/main/resources
fi

# Удаляем корневые тестовые файлы
if [ -d "src/test" ]; then
    echo "Removing root test directory..."
    rm -rf src/test
fi

# Удаляем корневой application.properties если существует
if [ -f "src/main/resources/application.properties" ]; then
    rm src/main/resources/application.properties
fi

# Удаляем корневой build если существует
if [ -d "build" ]; then
    echo "Cleaning root build directory..."
    rm -rf build
fi

# Создаем базовую структуру для подпроектов если не существует
mkdir -p src/{api-gateway,common-lib,discovery-service,menu-service,order-service,payment-service,user-service}/src/main/java/ru/otus/cafe

echo "Project cleanup completed!"
echo "You can now run: ./gradlew clean build"