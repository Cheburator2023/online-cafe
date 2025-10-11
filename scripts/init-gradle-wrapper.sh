#!/bin/bash

echo "Initializing Gradle Wrapper..."

# Создаем директорию для wrapper если её нет
mkdir -p gradle/wrapper

# Скачиваем актуальную версию Gradle Wrapper
gradle wrapper --gradle-version=8.5 --distribution-type=bin

# Даем права на выполнение
chmod +x ./gradlew

echo "Gradle Wrapper initialized successfully!"
echo "You can now run: ./gradlew build"