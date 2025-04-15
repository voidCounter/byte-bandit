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
./run-services.sh start
```

This command:

- Builds Docker images if they don’t exist
- Starts all services defined in `docker-compose.apps.yml`
- Waits for health checks to ensure services are ready

!!! tip
    Run `run-services.sh -h` to see all available actions and options.

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
- Check the [API Documentation](../api/api.md).

