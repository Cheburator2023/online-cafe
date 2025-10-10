#!/bin/bash

set -e

echo "Building microservices..."

# Build common library
cd src/common-lib
./gradlew build
cd ..

# Build all services
for service in user-service menu-service order-service payment-service api-gateway; do
    echo "Building $service..."
    cd $service
    ./gradlew build
    docker build -t victor2023victorovich/${service}:latest .
    cd ..
done

echo "Deploying to Kubernetes..."

# Deploy using Helm
cd ../helm/umbrella
helm dependency update
helm upgrade --install online-cafe . \
    --namespace online-cafe \
    --create-namespace \
    --values values.yaml

echo "Deployment completed!"