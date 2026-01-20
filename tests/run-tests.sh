#!/bin/bash

# Скрипт для запуска тестов K6

# Переходим в директорию скрипта
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "Текущая директория: $(pwd)"
echo "Содержимое директории:"
ls -la
echo "Содержимое tests/:"
ls -la tests/ 2>/dev/null || echo "Папка tests/ не существует"

echo "🚀 Запуск тестов для Online Cafe Microservices"

# Проверка наличия K6
if ! command -v k6 &> /dev/null; then
    echo "❌ K6 не установлен. Установите его: https://k6.io/docs/getting-started/installation/"
    exit 1
fi

# Проверка доступности сервисов
echo "🔍 Проверка доступности сервисов..."
services=("http://localhost:8761" "http://localhost:8080" "http://localhost:8000")
for service in "${services[@]}"; do
    if curl -s --head --request GET "$service" | grep "200\|302" > /dev/null; then
        echo "✅ $service доступен"
    else
        echo "❌ $service недоступен"
    fi
done

# Меню тестов
echo ""
echo "📋 Выберите тип тестов:"
echo "1) Дымовые тесты (smoke)"
echo "2) Интеграционные тесты (integration)"
echo "3) Нагрузочные тесты (load)"
echo "4) Стресс-тесты (stress)"
echo "5) Тестирование очередей (mq-test)"
echo "6) Запустить все тесты"
echo "7) Запустить дебаг тесты"
echo ""

read -p "Введите номер: " choice

case $choice in
    1)
        echo "🔄 Запуск дымовых тестов..."
        if [ -f "smoke/smoke-test.js" ]; then
            k6 run smoke/smoke-test.js
        else
            echo "❌ Файл smoke/smoke-test.js не найден"
        fi
        ;;
    2)
        echo "🔄 Запуск интеграционных тестов..."
        if [ -f "integration/full-flow.js" ]; then
            k6 run integration/full-flow.js
        else
            echo "❌ Файл integration/full-flow.js не найден"
        fi
        ;;
    3)
        echo "🔄 Запуск нагрузочных тестов..."
        if [ -f "load/user-scenario.js" ]; then
            k6 run load/user-scenario.js
        else
            echo "❌ Файл load/user-scenario.js не найден"
        fi
        ;;
    4)
        echo "🔄 Запуск стресс-тестов..."
        if [ -f "load/stress-test.js" ]; then
            k6 run load/stress-test.js
        else
            echo "❌ Файл load/stress-test.js не найден"
        fi
        ;;
    5)
        echo "🔄 Тестирование очередей..."
        if [ -f "integration/message-queue.js" ]; then
            k6 run integration/message-queue.js
        else
            echo "❌ Файл integration/message-queue.js не найден"
        fi
        ;;
    6)
        echo "🔄 Запуск всех тестов..."

        echo "=== 1. Дымовые тесты ==="
        if [ -f "smoke/smoke-test.js" ]; then
            k6 run smoke/smoke-test.js
        else
            echo "❌ Файл smoke/smoke-test.js не найден"
        fi

        echo "=== 2. Интеграционные тесты ==="
        if [ -f "integration/full-flow.js" ]; then
            k6 run integration/full-flow.js
        else
            echo "❌ Файл integration/full-flow.js не найден"
        fi

        echo "=== 3. Нагрузочные тесты ==="
        if [ -f "load/user-scenario.js" ]; then
            k6 run load/user-scenario.js
        else
            echo "❌ Файл load/user-scenario.js не найден"
        fi

        echo "=== 4. Стресс-тесты ==="
        if [ -f "load/stress-test.js" ]; then
            k6 run load/stress-test.js
        else
            echo "❌ Файл load/stress-test.js не найден"
        fi

        echo "=== 5. Тестирование очередей ==="
        if [ -f "integration/message-queue.js" ]; then
            k6 run integration/message-queue.js
        else
            echo "❌ Файл integration/message-queue.js не найден"
        fi
        ;;
    7) echo "🔄 Дебаг тестирование..."
               if [ -f "integration/debug-integration.js" ]; then
                   k6 run integration/debug-integration.js
               else
                   echo "❌ Файл integration/debug-integration.js не найден"
               fi
               ;;
    *)
        echo "❌ Неверный выбор"
        exit 1
        ;;
esac

echo ""
echo "✅ Тестирование завершено!"