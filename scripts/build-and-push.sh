#!/bin/bash

set -e

echo "Building and pushing Docker images..."

# Build common library first
echo "Building common library..."
cd src/common-lib
./gradlew build
cd ../..

# Build and push each service
SERVICES=("user-service" "menu-service" "order-service" "payment-service" "api-gateway" "discovery-service")

for SERVICE in "${SERVICES[@]}"; do
    echo "Building $SERVICE..."
    cd "src/$SERVICE"
    ./gradlew bootBuildImage --imageName="victor2023victorovich/${SERVICE}:latest"
    docker push "victor2023victorovich/${SERVICE}:latest"
    cd ../..
done

echo "All images built and pushed successfully!"