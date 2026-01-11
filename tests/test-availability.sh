#!/bin/bash

set -e

echo "🚀 Запуск тестов доступности для Online Cafe Microservices"

# Функция для проверки доступности сервиса с повторными попытками
check_service_availability() {
    local url=$1
    local service_name=$2
    local max_attempts=3
    local attempt=1
    local wait_time=2

    echo "🔍 Проверка доступности $service_name ($url)..."

    while [ $attempt -le $max_attempts ]; do
        if curl -s -f --max-time 5 "$url" > /dev/null 2>&1; then
            echo "✅ $service_name доступен (попытка $attempt из $max_attempts)"
            return 0
        elif curl -s --head --request GET "$url" | grep "200\|302" > /dev/null; then
            echo "✅ $service_name доступен (попытка $attempt из $max_attempts)"
            return 0
        else
            echo "⏳ $service_name недоступен, повторная попытка через ${wait_time}с... (попытка $attempt из $max_attempts)"
            sleep $wait_time
            attempt=$((attempt + 1))
        fi
    done

    echo "❌ $service_name недоступен после $max_attempts попыток"
    return 1
}

# Основные сервисы для проверки
declare -A services=(
    ["http://localhost:8761"]="Eureka Discovery Service"
    ["http://localhost:8080"]="API Gateway"
    ["http://localhost:8000"]="User Service"
    ["http://localhost:8001"]="Menu Service"
    ["http://localhost:8002"]="Order Service"
    ["http://localhost:8003"]="Payment Service"
)

# Проверка каждого сервиса
failed_services=()

for url in "${!services[@]}"; do
    if ! check_service_availability "$url" "${services[$url]}"; then
        failed_services+=("${services[$url]}")
    fi
done

# Вывод результатов
if [ ${#failed_services[@]} -eq 0 ]; then
    echo ""
    echo "🎉 Все сервисы успешно запущены и доступны!"
    echo ""
    echo "Доступные endpoints:"
    echo "  • Eureka Dashboard: http://localhost:8761"
    echo "  • API Gateway: http://localhost:8080"
    echo "  • User Service: http://localhost:8000"
    echo "  • Menu Service: http://localhost:8001"
    echo "  • Order Service: http://localhost:8002"
    echo "  • Payment Service: http://localhost:8003"
    echo "  • API Gateway Actuator: http://localhost:8080/actuator/health"
    echo "  • API Gateway Info: http://localhost:8080/actuator/info"
    echo ""
else
    echo ""
    echo "❌ Некоторые сервисы не доступны:"
    for service in "${failed_services[@]}"; do
        echo "  • $service"
    done
    echo ""
    echo "Рекомендации:"
    echo "  1. Проверьте логи Docker: docker-compose logs"
    echo "  2. Убедитесь, что все контейнеры запущены: docker-compose ps"
    echo "  3. Проверьте сетевые настройки: docker network inspect online-cafe_cafe-network"
    echo "  4. Перезапустите сервисы: docker-compose restart"
    echo ""
    exit 1
fi