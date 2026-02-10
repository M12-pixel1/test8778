#!/bin/bash
# Prometheus AgeTech - Deployment Script

set -e

echo "=== Prometheus AgeTech Deployment ==="

# Configuration
DOCKER_IMAGE="prometheus-agetech-backend"
DOCKER_TAG="${1:-latest}"

echo "Deploying version: $DOCKER_TAG"

# Build Docker image
echo "Building Docker image..."
docker build -t "$DOCKER_IMAGE:$DOCKER_TAG" ./backend

# Run with Docker Compose
echo "Starting services with Docker Compose..."
docker-compose up -d --build

echo ""
echo "=== Deployment Complete ==="
echo "API available at: http://localhost:8000"
echo "API docs at: http://localhost:8000/docs"
