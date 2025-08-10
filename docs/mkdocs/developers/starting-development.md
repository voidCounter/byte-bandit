# Starting Development

This guide will walk you through setting up your development environment and getting Byte Bandit running locally on your machine.

## Prerequisites Check

Before proceeding, ensure you have all the required tools installed:

```bash
# Check Java version (should be 17+)
java -version

# Check Maven version (should be 3.6+)
mvn --version

# Check Node.js version (should be 18+)
node --version
npm --version

# Check Docker version
docker --version
docker-compose --version

# Check Git version
git --version
```

If any of these commands fail, please refer to the [Before You Start](before-you-start.md) guide for installation instructions.

## Initial Setup

### 1. Fork and Clone the Repository

1. **Fork the repository** on GitHub:
   - Go to [https://github.com/Learnathon-By-Geeky-Solutions/byte-bandit](https://github.com/Learnathon-By-Geeky-Solutions/byte-bandit)
   - Click the "Fork" button to create your copy

2. **Clone your fork locally**:
   ```bash
   git clone https://github.com/YOUR_USERNAME/byte-bandit.git
   cd byte-bandit
   ```

3. **Add upstream remote** for syncing with the main repository:
   ```bash
   git remote add upstream https://github.com/Learnathon-By-Geeky-Solutions/byte-bandit.git
   ```

### 2. Environment Configuration

1. **Create environment file**:
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` file** with your configuration:
   ```bash
   # Required: Database Configuration
   POSTGRES_PASSWORD=postgres
   POSTGRES_USER=postgres
   POSTGRES_DB=postgres
   
   # Required: JWT Configuration
   JWT_SECRET=your-super-secret-jwt-key-here
   
   # Required: CORS Configuration
   CORS_ALLOWED_ORIGINS=http://localhost:3000
   
   # Optional: Email Configuration (for development)
   MAIL_USER_NAME=user@example.com
   MAIL_PASSWORD=securepassword
   
   # Optional: Google OAuth (if implementing OAuth)
   GOOGLE_OAUTH_CLIENT_ID=[Your Google Client ID]
   GOOGLE_OAUTH_CLIENT_SECRET=[Your Google Client Secret]
   GOOGLE_OAUTH_REDIRECT_URI=http://localhost:8084/api/v1/auth/google/callback
   
   # Optional: AWS S3 (for file storage)
   AWS_ACCESS_KEY_ID=[Your AWS Access Key ID]
   AWS_SECRET_ACCESS_KEY=[Your AWS Secret Access Key]
   AWS_REGION=[Your AWS Region]
   BUCKET_NAME=[Your AWS Bucket Name]
   ```

   **Note**: For local development, you can use the default values for database and JWT. The other configurations are optional and can be added later.

## Backend Development Setup

### 1. Build Backend Services

1. **Navigate to backend directory**:
   ```bash
   cd apps/backend
   ```

2. **Build all services**:
   ```bash
   mvn clean install
   ```

   This command will:
   - Download all dependencies
   - Compile all services
   - Run tests
   - Install artifacts to local Maven repository

3. **Verify build success**:
   ```bash
   # Check if all services built successfully
   ls -la */target/*.jar
   ```

### 2. Configure IntelliJ IDEA (Recommended)

1. **Open the project** in IntelliJ IDEA:
   - File → Open → Select the `byte-bandit` folder
   - Wait for project indexing to complete

2. **Install CheckStyle plugin**:
   - Go to Preferences → Plugins
   - Search for "CheckStyle-IDEA"
   - Install and restart IntelliJ

3. **Configure CheckStyle**:
   - Go to Preferences → Tools → CheckStyle
   - Add new configuration: Select `checkstyle.xml` from `apps/backend/`
   - Name it "checkstyle" and set as active

4. **Import code style**:
   - Go to Preferences → Editor → Code Style
   - Click gear icon → Import Scheme → IntelliJ IDEA code style XML
   - Select `IdeaJavaCodeStyle.xml` from `.idea/codeStyles/`

### 3. Run Backend Services Locally

#### Option A: Using Docker Compose (Recommended)

1. **Start infrastructure services**:
   ```bash
   cd ../../  # Return to project root
   docker-compose -f docker-compose.apps.yml up -d discovery-server config-server user-dev-db file-dev-db mailhog kafka zookeeper
   ```

2. **Wait for services to be ready**:
   ```bash
   # Check service status
   docker-compose -f docker-compose.apps.yml ps
   
   # Check logs for any errors
   docker-compose -f docker-compose.apps.yml logs discovery-server
   ```

3. **Start backend services**:
   ```bash
   docker-compose -f docker-compose.apps.yml up -d gateway user-service file-service
   ```

#### Option B: Running Services Individually

1. **Start Discovery Server**:
   ```bash
   cd apps/backend/discovery-server
   mvn spring-boot:run
   ```

2. **Start Config Server** (in new terminal):
   ```bash
   cd apps/backend/config-server
   mvn spring-boot:run
   ```

3. **Start User Service** (in new terminal):
   ```bash
   cd apps/backend/user-service
   mvn spring-boot:run
   ```

4. **Start File Service** (in new terminal):
   ```bash
   cd apps/backend/file-service
   mvn spring-boot:run
   ```

5. **Start Gateway** (in new terminal):
   ```bash
   cd apps/backend/gateway
   mvn spring-boot:run
   ```

### 4. Verify Backend Services

1. **Check service health**:
   - Discovery Server: [http://localhost:8761](http://localhost:8761)
   - Config Server: [http://localhost:8071](http://localhost:8071)
   - User Service: [http://localhost:8083](http://localhost:8083)
   - File Service: [http://localhost:8081](http://localhost:8081)
   - Gateway: [http://localhost:8084](http://localhost:8084)

2. **Test API endpoints**:
   ```bash
   # Test gateway health
   curl http://localhost:8084/actuator/health
   
   # Test user service
   curl http://localhost:8083/actuator/health
   
   # Test file service
   curl http://localhost:8081/actuator/health
   ```

## Frontend Development Setup

### 1. Install Dependencies

1. **Navigate to web directory**:
   ```bash
   cd apps/web
   ```

2. **Install Node.js dependencies**:
   ```bash
   npm install
   ```

3. **Verify installation**:
   ```bash
   npm list --depth=0
   ```

### 2. Configure Development Environment

1. **Create frontend environment file** (if needed):
   ```bash
   cp .env.example .env.local
   ```

2. **Update environment variables**:
   ```bash
   # API Gateway URL
   NEXT_PUBLIC_API_URL=http://localhost:8084
   
   # Development server port
   PORT=3000
   ```

### 3. Start Frontend Development Server

1. **Start development server**:
   ```bash
   npm run dev
   ```

2. **Verify frontend is running**:
   - Open [http://localhost:3000](http://localhost:3000) in your browser
   - You should see the Byte Bandit application

## Using the Service Management Script

For convenience, use the provided `run-services.sh` script:

### 1. Make it executable:
```bash
chmod +x run-services.sh
```

### 2. Available commands:
```bash
# Start all services
./run-services.sh start all

# Start only backend services
./run-services.sh start backend

# Start only infrastructure
./run-services.sh start infra

# Check service status
./run-services.sh ps

# Stop all services
./run-services.sh stop

# Restart specific service
./run-services.sh restart gateway

# View logs
./run-services.sh logs gateway

# Get help
./run-services.sh -h
```

## Development Workflow

### 1. **Daily Development Routine**

1. **Sync with upstream**:
   ```bash
   git fetch upstream
   git checkout develop
   git merge upstream/develop
   ```

2. **Create feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Make changes and test**:
   ```bash
   # Backend changes
   cd apps/backend
   mvn test
   
   # Frontend changes
   cd apps/web
   npm test
   ```

4. **Commit and push**:
   ```bash
   git add .
   git commit -m "feat(scope): description of changes"
   git push origin feature/your-feature-name
   ```

### 2. **Running Tests**

#### Backend Tests
```bash
cd apps/backend

# Run all tests
mvn test

# Run specific service tests
cd user-service
mvn test

# Run with coverage
mvn jacoco:report
```

#### Frontend Tests
```bash
cd apps/web

# Run unit tests
npm test

# Run tests in watch mode
npm test -- --watch

# Run tests with coverage
npm test -- --coverage
```

### 3. **Code Quality Checks**

#### Backend Quality
```bash
cd apps/backend

# Run Checkstyle
mvn checkstyle:check

# Run SonarQube analysis
mvn sonar:sonar
```

#### Frontend Quality
```bash
cd apps/web

# Run ESLint
npm run lint

# Run type checking
npm run type-check

# Format code
npm run format
```

## Troubleshooting

### Common Issues and Solutions

#### 1. **Port Already in Use**
```bash
# Find process using port
lsof -i :8084

# Kill process
kill -9 <PID>
```

#### 2. **Database Connection Issues**
```bash
# Check if PostgreSQL is running
docker ps | grep postgres

# Restart database
docker-compose -f docker-compose.apps.yml restart user-dev-db file-dev-db
```

#### 3. **Maven Build Failures**
```bash
# Clean and rebuild
mvn clean install -U

# Check Java version compatibility
java -version
```

#### 4. **Node.js Issues**
```bash
# Clear npm cache
npm cache clean --force

# Remove node_modules and reinstall
rm -rf node_modules package-lock.json
npm install
```

#### 5. **Docker Issues**
```bash
# Clean Docker environment
docker system prune -a

# Rebuild images
docker-compose -f docker-compose.apps.yml build --no-cache
```

### Getting Help

1. **Check service logs**:
   ```bash
   docker-compose -f docker-compose.apps.yml logs [service-name]
   ```

2. **Verify service health**:
   - Check individual service health endpoints
   - Review Eureka dashboard at [http://localhost:8761](http://localhost:8761)

3. **Check configuration**:
   - Verify `.env` file settings
   - Check `bootstrap.yaml` configurations
   - Review Docker Compose service definitions

## Next Steps

1. **Explore the codebase** using the [Project Structure](project-structure.md) guide
2. **Follow coding standards** from the [Code Style](code-style.md) guide
3. **Understand Git workflow** from the [Branching Strategy](branching-strategy.md) guide
4. **Start with a small issue** to get familiar with the development process
5. **Join team discussions** and code reviews

## Development Tips

- **Use IntelliJ IDEA** with CheckStyle plugin for consistent Java code
- **Run tests frequently** to catch issues early
- **Follow the branching strategy** for organized development
- **Update documentation** when making significant changes
- **Ask questions** during code reviews - it's how we all learn
