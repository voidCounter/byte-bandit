#!/bin/bash

BACKEND_SERVICES="discovery-server config-server gateway user-service file-service sync-service"
FRONTEND_SERVICE="client"
DOCS_SERVICE="docs"
ALL_SERVICES="$BACKEND_SERVICES $FRONTEND_SERVICE $DOCS_SERVICE"

build_backend_service() {
  local service=$1
  echo "---------------------------------------"
  echo "Building backend service: $service..."

  cd "apps/backend/$service" || { echo "Error: Directory $service not found"; exit 1; }

  echo "Running mvn dockerfile:build for $service..."
  mvn package -DskipTests dockerfile:build || { echo "Error: Maven build failed for $service"; exit 1; }

  VERSION=$(grep -m1 "<version>" pom.xml | sed 's/.*<version>\(.*\)<\/version>.*/\1/')
  if [ -z "$VERSION" ]; then
    echo "Error: Could not extract version from $service/pom.xml"
    exit 1
  fi
  echo "Extracted version from pom.xml: $VERSION"

  IMAGE_NAME="oakcan/$service"
  VERSIONED_IMAGE="$IMAGE_NAME:$VERSION"
  LATEST_IMAGE="$IMAGE_NAME:latest"

  echo "Tagging $VERSIONED_IMAGE as $LATEST_IMAGE..."
  docker tag "$VERSIONED_IMAGE" "$LATEST_IMAGE" || { echo "Error: Failed to tag $service image as latest"; exit 1; }

  echo "$service built and tagged: $VERSIONED_IMAGE and $LATEST_IMAGE"
  cd ../../../
}

# Function to build non-Maven service with Docker
build_non_maven_service() {
  local service=$1
  echo "---------------------------------------"
  echo "Building $service..."
  local dockerfile="Dockerfile"

  if [ "$service" = "client" ]; then
    cd "apps/web" || { echo "Error: Directory $service not found"; exit 1; }
  elif [ "$service" = "docs" ]; then
    if [ ! -d "docs" ]; then { echo "Error: Directory $service not found"; exit 1; }; fi
    dockerfile="./docs/Dockerfile"
  else
    echo "Error: Unknown service $service"
    exit 1
  fi

  IMAGE_NAME="oakcan/$service"
  LATEST_IMAGE="$IMAGE_NAME:latest"

  echo "Running docker build for $service..."
  docker build -t "$LATEST_IMAGE" . -f "$dockerfile" || { echo "Error: Docker build failed for $service"; exit 1; }

  echo "$service built and tagged: $LATEST_IMAGE"

  if [ "$service" = "client" ]; then
    cd ../../
  fi
}

# Function to check if a service is in a list
contains_service() {
  local service=$1
  local service_list="$2"
  echo "$service_list" | grep -qw "$service"
}

# Process arguments
if [ $# -eq 0 ]; then
  echo "No arguments provided. Building all services: $ALL_SERVICES"
  for service in $BACKEND_SERVICES; do
    build_backend_service "$service"
  done
  build_non_maven_service "$FRONTEND_SERVICE"
  build_non_maven_service "$DOCS_SERVICE"
else
  for arg in "$@"; do
    case "$arg" in
      "backend")
        echo "Building all backend services: $BACKEND_SERVICES"
        for service in $BACKEND_SERVICES; do
          build_backend_service "$service"
        done
        ;;
      "client")
        echo "Building frontend service: $FRONTEND_SERVICE"
        build_non_maven_service "$FRONTEND_SERVICE"
        ;;
      "docs")
        echo "Building docs service: $DOCS_SERVICE"
        build_non_maven_service "$DOCS_SERVICE"
        ;;
      *)
        # Check if it's a specific service
        if contains_service "$arg" "$ALL_SERVICES"; then
          if contains_service "$arg" "$BACKEND_SERVICES"; then
            build_backend_service "$arg"
          else
            build_non_maven_service "$arg"
          fi
        else
          echo "Error: Unknown service or option '$arg'. Valid options: backend, client, docs, or a specific service ($ALL_SERVICES)"
          exit 1
        fi
        ;;
    esac
  done
fi

echo "---------------------------------------"
echo "Build process completed successfully!"