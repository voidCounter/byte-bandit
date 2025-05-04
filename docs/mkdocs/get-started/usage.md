# Usage Guide

!!! Note
    Please follow the instructions in the [Local Setup](./local-setup.md) guide to set up the Oakcan application locally.

Once the Oakcan services are up and running, you can interact with the application through the frontend, APIs, and other components. Follow these steps to use the application locally:

### 1. Access the Frontend
- Open your browser and navigate to [http://localhost:3000](http://localhost:3000).
- The main web application should load, displaying the Oakcan user interface.
- **Sign Up or Log In**:
    - Use the "Sign Up" option to create a new account. You can register using an email and password or via Google OAuth (if configured in the `.env` file).
    - After signing up, check the MailHog UI ([http://localhost:8025](http://localhost:8025)) for a verification email.
    - Log in with your credentials to access the application's features.

### 2. Explore Key Features
The Oakcan application provides several core functionalities:

- **File Management**:
    - Upload files to the S3 bucket (configured via `.env` AWS credentials).
    - Create folders and organize files.
    - Share files, folders publicly or with specific users.
- **API Interaction**:
    - Access the API gateway at [http://localhost:8084](http://localhost:8084) to interact with backend services.
    - Refer to the [API Documentation](../api/overview.md) for available endpoints and usage examples.
- **Service Monitoring**:
    - Visit the Eureka dashboard at [http://localhost:8761](http://localhost:8761) to monitor the status of registered services (e.g., user-service, file-service).
    - Ensure all services are in the "UP" state.

### 3. Test Email Functionality
- Oakcan uses MailHog for email testing in the development environment.
- Access the MailHog UI at [http://localhost:8025](http://localhost:8025) to view all emails sent by the application (e.g., verification emails, password reset links).
- Verify that emails are being sent and received correctly for actions like account creation or password recovery.

### 4. View Documentation
- The documentation site is available at [http://localhost:8000](http://localhost:8000).
- Browse user guides for detailed instructions on using the application.
- Check the API documentation for information on interacting with backend services programmatically.

### 5. Troubleshooting Common Issues
If you encounter issues while using the application, try the following:

- **Frontend Issues**:
    - If the frontend at [http://localhost:3000](http://localhost:3000) doesn't load, ensure the `client` service is running (`docker ps | grep client`).
    - Restart the client service: `./run-services.sh restart client`.
    - Clear your browser cache to resolve potential caching issues.
- **API Issues**:
    - If API requests to [http://localhost:8084](http://localhost:8084) fail, check the gateway service logs: `docker compose -f docker-compose.apps.yml logs gateway`.
    - Verify that CORS settings in the `.env` file (`CORS_ALLOWED_ORIGINS`) include `http://localhost:3000`.
- **Email Issues**:
    - If no emails appear in MailHog, confirm the `mailhog` service is running (`docker ps | grep mailhog`).
    - Check the `user-service` logs for email-sending errors: `docker compose -f docker-compose.apps.yml logs user-service`.
- **Database Issues**:
    - If services fail to connect to the database, verify that `user-dev-db` and `file-dev-db` are healthy: `docker ps | grep dev-db`.
    - Inspect database logs for errors: `docker logs oakcan-user-dev-db` or `docker logs oakcan-file-dev-db`.

### 6. Stopping and Cleaning Up
When you're done using the application:
- Stop all services without removing containers: `./run-services.sh stop`.
- Stop and remove all containers and networks: `./run-services.sh down`.
- To restart specific services (e.g., after making changes): `./run-services.sh restart <service_name>`.

## Next Steps
- Dive deeper into the [API Documentation](../api/overview.md) to explore programmatic interactions with Oakcan.
- Experiment with uploading and managing files to understand the file-service capabilities.
- Monitor service health and performance via the Eureka dashboard for a better understanding of the microservices architecture.

For additional help, refer to the [Get Started guide](./local-setup.md) or the troubleshooting section.

