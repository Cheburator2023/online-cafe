#!/bin/bash

echo "Verifying project structure..."

# Проверяем наличие главных классов
services=("api-gateway" "discovery-service" "user-service" "menu-service" "order-service" "payment-service")

for service in "${services[@]}"; do
    echo "Checking $service..."

    # Проверяем наличие главного класса
    if [ -f "src/$service/src/main/java/ru/otus/cafe/${service//-//}/$(echo $service | sed 's/-service//' | awk '{print toupper(substr($0,1,1)) substr($0,2)}')Application.java" ]; then
        echo "✓ Main class found for $service"
    else
        echo "✗ Main class NOT found for $service"
        find "src/$service/src" -name "*.java" | head -5
    fi

    # Проверяем build.gradle.kts
    if grep -q "mainClass" "src/$service/build.gradle.kts"; then
        echo "✓ mainClass configured in build.gradle.kts"
    else
        echo "✗ mainClass NOT configured in build.gradle.kts"
    fi
done

echo "Verification completed!"