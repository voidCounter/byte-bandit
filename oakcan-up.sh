#!/bin/bash
set -e  # Exit on error

# List of backend services
services=("config-server" "discovery-server" "gateway" "user-service" "file-service" "sync-service" "client" "docs")

start_infrastructure() {
  echo "Starting infrastructure services..."
  # Get list of services from infra.yml
  infra_services=$(docker-compose -f docker-compose.infra.yml config --services)

  if [[ -z "$infra_services" ]]; then
    echo "No services found in docker-compose.infra.yml!"
    exit 1
  fi

  # Start all infra services
  for infra_service in $infra_services; do
    echo "Starting $infra_service..."
    docker-compose -f docker-compose.infra.yml up -d "$infra_service"
  done
}

# Function to rebuild and restart a specific service
rebuild_service() {
  local service=$1

  if [[ " ${services[*]} " =~ ${service} ]]; then
    if [[ "$service" == "docs" ]]; then
      echo "Building docs..."
      (cd "docs" && docker build -t oakcan/docs:0.0.1-SNAPSHOT -f Dockerfile ../)
    elif [[ "$service" == "client" ]]; then
      echo "Building client..."
      (cd "apps/web" && docker build -t oakcan/client:0.0.1-SNAPSHOT .)
    else
      echo "Building $service..."
      (cd "apps/backend/$service" && mvn package dockerfile:build)
    fi
  else
    echo "Service $service not found!"
    exit 1
  fi

  echo "Stopping $service..."
  docker-compose -f docker-compose.infra.yml -f docker-compose.apps.yml stop "$service"

  echo "Removing old container for $service..."
  docker-compose -f docker-compose.infra.yml -f docker-compose.apps.yml rm -f "$service"

  echo "Starting $service..."
  docker-compose -f docker-compose.infra.yml -f docker-compose.apps.yml up -d "$service"
}

# Full rebuild (if no arguments are provided)
full_rebuild() {
  echo "Starting full rebuild..."
  echo "Starting infrastructure services..."
  docker-compose -f docker-compose.infra.yml up -d --remove-orphans

  for service in "${services[@]}"; do
    rebuild_service "$service"
  done

  echo "Removing dangling images..."
  docker image prune -f

  echo "Starting all application services..."
  docker-compose -f docker-compose.infra.yml -f docker-compose.apps.yml up -d --remove-orphans
}

if [[ -n "$1" ]]; then
  # Skip infrastructure for 'client' or 'docs', run it for everything else
  case "$1" in
    client|docs)
      ;;
    *)
      start_infrastructure  # Run for all other services
      ;;
  esac
  rebuild_service "$1"  # Rebuild and restart only the specified service
else
  full_rebuild  # Rebuild all services if no argument is provided
fi