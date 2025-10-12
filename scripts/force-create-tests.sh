#!/bin/bash

echo "Force creating test structure..."

services=("api-gateway" "discovery-service" "user-service" "menu-service" "order-service" "payment-service")

for service in "${services[@]}"; do
    echo "Creating test for $service..."

    # Создаем директорию для тестов
    mkdir -p "src/$service/src/test/java/ru/otus/cafe/${service//-//}"
    mkdir -p "src/$service/src/test/resources"

    # Определяем имя класса приложения и пакет
    if [ "$service" == "user-service" ]; then
        app_class="UserApplication"
        package="user"
        full_package="ru.otus.user"
    else
        app_class=$(echo $service | sed 's/-service//' | awk '{print toupper(substr($0,1,1)) substr($0,2)}')"Application"
        package=$(echo $service | sed 's/-service//')
        full_package="ru.otus.cafe.${package}"
    fi

    # Создаем тестовый класс
    cat > "src/$service/src/test/java/ru/otus/cafe/${service//-//}/${app_class}Tests.java" << EOF
package ${full_package};

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ${app_class}Tests {

    @Test
    void contextLoads(ApplicationContext context) {
        assertNotNull(context);
    }

    @Test
    void mainMethodStartsApplication() {
        ${app_class}.main(new String[]{});
    }
}
EOF

    # Создаем тестовый application.yml
    cat > "src/$service/src/test/resources/application-test.yml" << EOF
spring:
  main:
    web-application-type: none
  cloud:
    discovery:
      enabled: false

logging:
  level:
    org.springframework: WARN
    ${full_package}: DEBUG
EOF

    echo "✓ Created test for $service"
done

echo "Test structure creation completed!"