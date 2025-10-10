# Создать структуру проекта
chmod +x scripts/setup-project.sh
./scripts/setup-project.sh

# Проверить синтаксис Gradle
./gradlew help

# Собрать весь проект
./gradlew build

# Собрать конкретный сервис
./gradlew :user-service:build
./gradlew :api-gateway:build