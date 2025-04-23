#!/bin/bash
set -e  # Exit on error

# List of backend services
COMPOSE_FILE="docker-compose.apps.yml"
INFRA_SERVICES="user-dev-db mailhog kafka zookeeper"
INFRA_TEST_SERVICES="test-db kafka-test zookeeper-test"
BACKEND_SERVICES="discovery-server config-server gateway user-service file-service sync-service"
FRONTEND_SERVICE="client"
DOCS_SERVICE="docs"
ALL_SERVICES="$BACKEND_SERVICES $FRONTEND_SERVICE $DOCS_SERVICE $INFRA_SERVICES"
ACTIVE_PROFILES="--profile infra --profile core"

# --- Helper Functions ---
usage() {
  echo "Usage: $0 <action> [options] [service...]"
  echo ""
  echo "Actions:"
  echo "  start   Build (if needed) and start specified services (or all core/infra if none specified)."
  echo "  stop    Stop specified services (or all)."
  echo "  restart Stop, remove, build (optional), and start specified services."
  echo "  down    Stop and remove all containers, networks defined in the compose file."
  echo ""
  echo "Options:"
  echo "  -t, --test       Run services in test mode (e.g., requires a docker-compose.test.yaml)."
  echo "  -n, --no-build   Skip the build step during 'start' or 'restart'."
  echo "  -h, --help       Show this help message."
  echo "Services:"
  echo "  <service_name>  Specify one or more service names (e.g., user-service gateway)."
  echo "  backend         Apply action to all backend services ($BACKEND_SERVICES)."
  echo "  infra           Apply action to all infrastructure services ($INFRA_SERVICES)."
  echo "  all             Apply action to all defined services ($ALL_SERVICES)."
  echo "  (none)          Default depends on action (e.g., 'start' defaults to core+infra, 'ps' shows all)."
  echo ""
  exit 1
}

ACTION=""
TARGET_SERVICES=()
NO_BUILD=false
TEST_MODE=false

run_compose() {
  echo "Running: docker compose -f $COMPOSE_FILE $ACTIVE_PROFILES $*"
  docker compose -f "$COMPOSE_FILE" $ACTIVE_PROFILES "$@"
}

# Function to check if a service is in a list
contains_service() {
  local service=$1
  local service_list="$2"
  echo "$service_list" | grep -qw "$service"
}


# Parse action first
if [[ $# -eq 0 ]]; then
  usage
fi
ACTION=$1
shift
if [[ "$ACTION" == "-h" || "$ACTION" == "--help" ]]; then
  usage
elif [[ "$ACTION" != "start" && "$ACTION" != "stop" && "$ACTION" != "restart" && "$ACTION" != "down" && "$ACTION" != "-h" && "$ACTION" != "--help" ]]; then
  echo "Error: Invalid action '$ACTION'. Valid actions are: start, stop, restart, down, -h, --help."
  exit 1
fi

while [[ $# -gt 0 ]]; do
  case "$1" in
    -n|--no-build)
      NO_BUILD=true
      shift
      ;;
    -t|--test)
      TEST_MODE=true
      COMPOSE_FILE="docker-compose.test.yaml"
      shift
      ;;
    backend)
      TARGET_SERVICES+=("$BACKEND_SERVICES")
      shift
      ;;
    infra)
      NO_BUILD=true # for infra, we don't need to build images
      if [[ "$TEST_MODE" == true ]]; then
        TARGET_SERVICES+=("$INFRA_TEST_SERVICES")
      else
        TARGET_SERVICES+=("$INFRA_SERVICES")
      fi
      shift
      ;;
    all)
      TARGET_SERVICES+=("$ALL_SERVICES")
      shift
      ;;
    -*)
      echo "Error: Invalid option '$1'. Use -h or --help for usage."
      exit 1
      ;;
    *) # it's a service name
      if contains_service "$1" "$ALL_SERVICES"; then
        TARGET_SERVICES+=("$1")
      else
        echo "Error: Unknown service '$1'. Valid options: backend, all, infra, or a specific service ($ALL_SERVICES)"
        exit 1
      fi
      shift
      ;;
  esac
done
# remove duplicates
mapfile -t TARGET_SERVICES < <(echo "${TARGET_SERVICES[@]}" | tr ' ' '\n' | sort -u)


echo "Action: $ACTION"
if [[ ${#TARGET_SERVICES[*]} -gt 0 ]]; then
    echo "Target(s): ${TARGET_SERVICES[*]}"
fi
echo "Options: No-Build=${NO_BUILD}"
echo "---"

case "$ACTION" in
  start)
    if [[ "$NO_BUILD" = false ]]; then
      echo "Building necessary images..."
      # Build only targets if specified, otherwise build based on profiles for 'up'
      if [[ ${#TARGET_SERVICES[@]} -gt 0 ]]; then
          chmod +x ./build-services.sh
          ./build-services.sh "${TARGET_SERVICES[@]}"
      fi
    else
      echo "Skipping build step..."
    fi
    echo "Starting services..."
    # Start specific targets, or default to core+infra profiles if no targets given
    run_compose up -d "${TARGET_SERVICES[@]}"
  ;;
  stop)
    echo "Stopping services..."
    run_compose stop "${TARGET_SERVICES[@]}"
  ;;
  restart)
    echo "Restarting services..."
    # 1. Stop
    run_compose stop "${TARGET_SERVICES[@]}"
    # 2. Remove
    run_compose rm -f "${TARGET_SERVICES[@]}"
    # 3. Build (if needed)
    if [[ "$NO_BUILD" = false ]]; then
      echo "Building necessary images..."
      # Build only targets if specified, otherwise build based on profiles for 'up'
      if [[ ${#TARGET_SERVICES[@]} -gt 0 ]]; then
          chmod +x ./build-services.sh
          ./build-services.sh "${TARGET_SERVICES[@]}"
      fi
    else
      echo "Skipping build step..."
    fi
    # 4. Start
    echo "Starting services..."
    run_compose up -d --remove-orphans "${TARGET_SERVICES[@]}"
  ;;
  down)
    echo "Stopping and removing all services..."
    run_compose down --remove-orphans
  ;;
  *)
    echo "Error: Unknown action '$ACTION'."
    exit 1
  ;;
esac
