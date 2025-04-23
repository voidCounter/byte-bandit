#!/bin/bash
set -e

SERVICES="user-service gateway file-service sync-service"

echo "Creating .env file in root..."
echo "POSTGRES_PASSWORD=$POSTGRES_PASSWORD" > .env
echo "JWT_SECRET=$JWT_SECRET" >> .env
echo "KAFKA_BOOTSTRAP_SERVERS=localhost:29093" >> .env

echo "Copying .env to service directories..."
for service in $SERVICES; do
  if [ ! -d "apps/backend/$service" ]; then
    echo "Directory apps/backend/$service does not exist. Skipping..."
    continue
  fi
  cp .env "apps/backend/$service/.env"
  echo "Copied .env to apps/backend/$service/"
done

echo "Environment setup complete."