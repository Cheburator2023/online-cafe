#!/bin/bash

set -e

echo "Starting Online Cafe Microservices deployment..."

# Variables
NAMESPACE="online-cafe"
HELM_CHART="./helm/umbrella"

# Create namespace if it doesn't exist
kubectl create namespace $NAMESPACE --dry-run=client -o yaml | kubectl apply -f -

# Deploy infrastructure components first
echo "Deploying infrastructure components..."
helm upgrade --install infrastructure ./helm/infrastructure \
    --namespace $NAMESPACE \
    --wait

# Wait for infrastructure to be ready
echo "Waiting for infrastructure to be ready..."
kubectl wait --for=condition=ready pod -l app=rabbitmq --namespace $NAMESPACE --timeout=300s
kubectl wait --for=condition=ready pod -l app=postgresql --namespace $NAMESPACE --timeout=300s

# Deploy the main application
echo "Deploying Online Cafe microservices..."
helm upgrade --install online-cafe $HELM_CHART \
    --namespace $NAMESPACE \
    --wait

# Wait for services to be ready
echo "Waiting for services to be ready..."
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=discovery-service --namespace $NAMESPACE --timeout=300s
kubectl wait --for=condition=ready pod -l app.kubernetes.io/name=api-gateway --namespace $NAMESPACE --timeout=300s

echo "Deployment completed successfully!"
echo ""
echo "Access points:"
echo "  API Gateway: http://localhost:8080"
echo "  Eureka Dashboard: http://localhost:8761"
echo "  Grafana: http://localhost:3000"
echo "  Prometheus: http://localhost:9090"
echo "  RabbitMQ Management: http://localhost:15672"