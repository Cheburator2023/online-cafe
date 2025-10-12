#!/bin/bash

echo "Setting up test structure..."

services=("api-gateway" "discovery-service" "user-service" "menu-service" "order-service" "payment-service")

for service in "${services[@]}"; do
    echo "Setting up tests for $service..."

    # Создаем директорию для тестов
    mkdir -p "src/$service/src/test/java/ru/otus/cafe/${service//-//}"

    # Определяем имя класса приложения
    if [ "$service" == "user-service" ]; then
        app_class="UserApplication"
        package="user"
    else
        app_class=$(echo $service | sed 's/-service//' | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"Application"
        package=$(echo $service | sed 's/-service//')
    fi

    # Создаем базовый тест
    cat > "src/$service/src/test/java/ru/otus/cafe/${service//-//}/${app_class}Tests.java" << EOF
package ru.otus.cafe.${package};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ${app_class}Tests {

    @Test
    void contextLoads() {
    }

}
EOF

    echo "✓ Created test for $service"
done

echo "Test structure setup completed!"