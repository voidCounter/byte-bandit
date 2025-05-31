# Get Started

This guide helps you quickly run the Oakcan application using Docker. With a single command, you’ll have the full
application up and running, including the frontend, backend services, database, and documentation site.

!!! info  
    Docker is the easiest way to try Oakcan, as it handles all dependencies automatically. You don’t need to install Java,
    Node.js, or other tools manually.

---

## **Prerequisites**

Ensure you have:

- **Docker** and **Docker Compose** installed ([Install Docker](https://docs.docker.com/get-docker/))
- **Git** to clone the repository

Clone the Oakcan repository:

```bash
git clone https://github.com/oakcan/oakcan.git
cd oakcan
```

### Step 1: Configure Environment Variables

- Create a .env file in the project root to set up necessary configurations:

```txt
POSTGRES_PASSWORD=postgres
POSTGRES_USER=postgres
POSTGRES_DB=postgres
MAIL_USER_NAME=user@example.com
MAIL_PASSWORD=securepassword
JWT_SECRET=450685e30cb943fb380c10783534e1ddb854cf9d388ea6a2a9c60cf34787ede70048fe37dc7fde8f9ac0ffae3f76cf0c28397a0d0e2027dc4de17da2e935d6a8b362870fed866d910eebbe13fd5ee6fdb1143956e3679a87219f97f9ce27b19716e4fb6d31a97f97366cb93cdfa2c9980002900c77a439f0eb45ba7d572659925a476bcfd4c3bcef1565e6aa2c214f210ca7b0bf8c0f61abfe16f86abe3694dce3b95ec5f645cbb00775f1c37757ee841a7af69fb7599a04325f3e1480f2cd97f2bd0409fe3d62d6cdbfa244a49d82d16eae220598cc7cb763fef158d1304ea5c4266c64fb4b5667f99127f3714efd825d4ea514a82b20edad266ac9dee84e4e
CORS_ALLOWED_ORIGINS=http://localhost:3000
# GOOGLE OAUTH
GOOGLE_OAUTH_CLIENT_ID=[Your Google Client ID]
GOOGLE_OAUTH_CLIENT_SECRET=[Your Google Client Secret]
GOOGLE_OAUTH_REDIRECT_URI=http://localhost:8084/api/v1/auth/google/callback
GOOGLE_OAUTH_TOKEN_ENDPOINT=https://oauth2.googleapis.com/token
GOOGLE_OAUTH_USERINFO_ENDPOINT=https://www.googleapis.com/oauth2/v3/userinfo

DEV_CLIENT_HOST_URI=http://localhost:3000

# AWS S3
AWS_ACCESS_KEY_ID=[Your Aws Access Key ID]
AWS_SECRET_ACCESS_KEY=[Your Aws Secret Access Key]
AWS_REGION=[Your Aws Region]
BUCKET_NAME=[Your Aws Bucket Name]


```

### Step 2: Run the Application

Use the provided `run-services.sh`(in the root folder) script to start all services, including the frontend, backend,
database, email server, and
documentation site. Make sure to make it executable before running it:

```bash
chmod +x run-services.sh
```

Then, run the script to start the services:

```bash 
./run-services.sh start all
```

This command:

- Builds Docker images if they don’t exist
- Starts all services defined in `docker-compose.apps.yml`
- Waits for health checks to ensure services are ready

!!! tip
    Run `run-services.sh -h` to see all available actions and options.
Here are the options:
```txt
Usage: ./run-services.sh <action> [options] [service...]

Actions:
start   Build (if needed) and start specified services (or all core/infra if none specified).
stop    Stop specified services (or all).
restart Stop, remove, build (optional), and start specified services.
down    Stop and remove all containers, networks defined in the compose file.

Options:
-t, --test       Run services in test mode (e.g., requires a docker-compose.test.yaml).
-n, --no-build   Skip the build step during 'start' or 'restart'.
-h, --help       Show this help message.
Services:
<service_name>  Specify one or more service names (e.g., user-service gateway).
backend         Apply action to all backend services (discovery-server config-server gateway user-service file-service).
infra           Apply action to all infrastructure services (user-dev-db file-dev-db mailhog kafka zookeeper).
all             Apply action to all defined services (discovery-server config-server gateway user-service file-service client docs user-dev-db file-dev-db mailhog kafka zookeeper).
(none)          Default depends on action (e.g., 'start' defaults to core+infra, 'ps' shows all).
```
It should run all the applications and services in the background. You can check the status of the services with 
`docker ps`:
```bash 
❯ docker ps
CONTAINER ID   IMAGE                              COMMAND                  CREATED             STATUS                       PORTS                                                                                  NAMES
319f17ca891d   oakcan/file-service:latest         "java -jar applicati…"   About an hour ago   Up 2 minutes                 0.0.0.0:8081->8081/tcp, :::8081->8081/tcp                                              oakcan-file-service
332ca27091eb   oakcan/user-service:latest         "java -jar applicati…"   About an hour ago   Up 2 minutes                 0.0.0.0:8083->8083/tcp, :::8083->8083/tcp                                              oakcan-user-service
b37ffbd96674   oakcan/gateway:latest              "java -jar applicati…"   About an hour ago   Up 6 minutes (healthy)       0.0.0.0:8084->8084/tcp, :::8084->8084/tcp                                              oakcan-gateway
d35705e7dbc4   confluentinc/cp-kafka:latest       "/etc/confluent/dock…"   About an hour ago   Up 27 minutes (healthy)      9092/tcp, 0.0.0.0:29092->29092/tcp, :::29092->29092/tcp                                byte-bandit-kafka-1
0750bab75a35   oakcan/config-server:latest        "java -jar applicati…"   About an hour ago   Up 27 minutes (healthy)      0.0.0.0:8071->8071/tcp, :::8071->8071/tcp                                              oakcan-config-server
ff040ab5dd73   confluentinc/cp-zookeeper:latest   "/etc/confluent/dock…"   About an hour ago   Up About an hour (healthy)   2888/tcp, 0.0.0.0:2181->2181/tcp, :::2181->2181/tcp, 3888/tcp                          byte-bandit-zookeeper-1
fd948eb79aad   oakcan/discovery-server:latest     "java -jar applicati…"   About an hour ago   Up About an hour (healthy)   0.0.0.0:8761->8761/tcp, :::8761->8761/tcp                                              oakcan-discovery-server
5e039a76069d   postgres:17-alpine                 "docker-entrypoint.s…"   About an hour ago   Up 2 minutes (healthy)       0.0.0.0:15432->5432/tcp, [::]:15432->5432/tcp                                          user-dev-db
dcc5e9efb399   postgres:17-alpine                 "docker-entrypoint.s…"   About an hour ago   Up About an hour (healthy)   0.0.0.0:15532->5432/tcp, [::]:15532->5432/tcp                                          file-dev-db
f44985cc6f28   oakcan/client:latest               "docker-entrypoint.s…"   About an hour ago   Up About an hour             0.0.0.0:3000->3000/tcp, :::3000->3000/tcp                                              oakcan-client
08583148b717   mailhog/mailhog                    "MailHog"                About an hour ago   Up About an hour (healthy)   0.0.0.0:1025->1025/tcp, :::1025->1025/tcp, 0.0.0.0:8025->8025/tcp, :::8025->8025/tcp   mailhog
```

If any service or application is not running for some reason, you can restart the service with the following command:
```bash
./run-services.sh restart <service_name>
```
To skip build step, use the `-n` option:
```bash 
./run-services.sh restart -n <service_name>
```
### Step 3: access the application

Once the services are running, you can access oakcan components:

| component         |                    url/port                    |                   description |
|:------------------|:----------------------------------------------:|------------------------------:|
| frontend          | [http://localhost:3000](http://localhost:3000) |          main web application |
| api gateway       | [http://localhost:8084](http://localhost:8084) |       backend api entry point |
| documentation     | [http://localhost:8000](http://localhost:8000) |      user guides and api docs |
| email testing     | [http://localhost:8025](http://localhost:8025) | mailhog ui for viewing emails |
| service dashboard | [http://localhost:8761](http://localhost:8761) | eureka dashboard for services |

### Step 4: Verify Everything Works

Confirm services are running: `docker ps`

- View logs if something isn’t working: `docker compose -f docker-compose.apps.yml logs [service_name]`
- Visit the Eureka dashboard ([http://localhost:8761](http://localhost:8761)) to see registered services.
- Test the frontend by navigating to [http://localhost:3000](http://localhost:3000) and performing actions like signing
  up.

Stopping the Application
To stop all services without removing them:

```bash
./run-services.sh stop
```

To stop and remove all containers and networks:

```bash
./run-services.sh down
```

!!! Troubleshooting
    1. Port conflicts: Ensure ports (`3000`, `8084`, `8761`, `15432`, `8025`, `8000`) are free. Check with `lsof -i :port`
    and stop conflicting processes.
    2. Services not starting: View logs (e.g., `docker logs oakcan-user-dev-db`) to diagnose issues, such as database
    connection errors.
    3. No emails in MailHog: Confirm the MailHog UI ([http://localhost:8025](http://localhost:8025)) is accessible and the
    service is healthy (docker ps).
    4. Frontend not loading: Clear your browser cache or restart the client service (`./run-services.sh restart client`).

### **Next Steps**

- Explore the [User Guide](./usage.md) to learn how to use the application.
- Check the [API Documentation](../api/overview.md).

