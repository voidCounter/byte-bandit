#!/bin/bash
set -e  # Exit on error

# List of backend services
BACKEND_SERVICES="discovery-server config-server gateway user-service file-service sync-service"
FRONTEND_SERVICE="client"
DOCS_SERVICE="docs"
ALL_SERVICES="$BACKEND_SERVICES $FRONTEND_SERVICE $DOCS_SERVICE"

# Function to rebuild and restart a specific service
rebuild_service() {
  local service=$1

   if [[ " ${ALL_SERVICES[*]} " =~ ${service} ]]; then
    ./build-services.sh "$service"  # Build the specified service
    if ! ./build-services.sh "$service"; then  # Build the specified service
      echo "ERROR: FAILED TO BUILD $service"
      exit 1
    fi
   else
     echo "Service $service not found!"
     exit 1
   fi

  echo "Stopping $service..."
  docker-compose -f docker-compose.apps.yml stop "$service" || { echo "ERROR: FAILED TO STOP $service"; exit 1; }

  echo "Removing old container for $service..."
  docker-compose -f docker-compose.apps.yml rm -f "$service" || { echo "ERROR: FAILED TO REMOVE $service CONTAINER"; exit 1; }

  echo "Starting $service..."
  docker-compose -f docker-compose.apps.yml up -d "$service" || { echo "ERROR: FAILED TO START $service"; exit 1; }
}

# Full rebuild (if no arguments are provided)
full_rebuild() {
  for service in $ALL_SERVICES; do
    rebuild_service "$service"
  done

  echo "Removing dangling images..."
  docker image prune -f

  echo "Starting all application services..."
  docker-compose -f docker-compose.apps.yml up -d --remove-orphans
}

if [[ -n "$1" ]]; then
  if [ "$1" = "backend" ]; then
    echo "Rebuilding all backend services..."
    for service in $BACKEND_SERVICES; do
      rebuild_service "$service"  # Rebuild and restart all backend services
    done
  else
  rebuild_service "$1"  # Rebuild and restart only the specified service
  fi
else
  full_rebuild  # Rebuild all services if no argument is provided
fi