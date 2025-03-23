#!/bin/bash
set -e  # Exit on error

services=("discovery" "gateway")

for service in "${services[@]}"; do
  echo "Building $service..."
  (cd "$service" && mvn package dockerfile:build)
done

echo "Removing dangling images..."
docker image prune -f

echo "Starting Docker Compose..."
docker-compose up --build
