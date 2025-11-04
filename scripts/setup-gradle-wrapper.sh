#!/bin/bash
set -e

echo "Setting up Gradle Wrapper..."

# Скачиваем Gradle Wrapper если не существует
if [ ! -f "gradlew" ]; then
    echo "Gradle Wrapper not found, initializing..."
    gradle wrapper --gradle-version=8.5
fi

# Даем права на выполнение
chmod +x gradlew

echo "Gradle Wrapper setup completed"