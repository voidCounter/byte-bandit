# Project Structure

This document provides an overview of the Byte Bandit project structure, explaining how the codebase is organized and where to find different components.

## Overview

Byte Bandit follows a **monorepo architecture** with clear separation between backend microservices and frontend applications. The project is organized to support independent development of services while maintaining shared configurations and documentation.

## Root Directory Structure

```
byte-bandit/
├── apps/                    # Application modules
│   ├── backend/            # Backend microservices
│   └── web/                # Next.js frontend application
├── docs/                   # Project documentation (MkDocs)
├── public/                 # Public assets and images
├── .github/                # GitHub Actions workflows
├── docker-compose.apps.yml # Main Docker Compose configuration
├── docker-compose.test.yaml # Test environment configuration
├── run-services.sh         # Service management script
├── build-services.sh       # Service building script
└── pom.xml                 # Root Maven configuration
```

## Backend Services (`apps/backend/`)

The backend follows a **microservices architecture** with Spring Boot applications:

### Core Services

```
backend/
├── gateway/                # API Gateway (Port 8084)
│   ├── src/main/java/com/bytebandit/gateway/
│   │   ├── controller/     # REST endpoints
│   │   ├── config/         # Routing and security configuration
│   │   └── filter/         # Request/response filters
│   └── src/main/resources/
│       └── bootstrap.yaml  # Service configuration
├── user-service/           # User management service (Port 8083)
│   ├── src/main/java/com/bytebandit/userservice/
│   │   ├── controller/     # User registration, authentication
│   │   ├── service/        # Business logic
│   │   ├── repository/     # Data access layer
│   │   └── model/          # Data models
│   └── src/main/resources/
│       ├── bootstrap.yaml  # Service configuration
│       ├── schema.sql      # Database schema
│       └── db/migration/   # Flyway migrations
├── file-service/           # File operations service (Port 8081)
│   ├── src/main/java/com/bytebandit/fileservice/
│   │   ├── controller/     # File CRUD operations
│   │   ├── service/        # File business logic
│   │   ├── repository/     # Data access
│   │   └── model/          # File system models
│   └── src/main/resources/
│       ├── bootstrap.yaml  # Service configuration
│       ├── schema.sql      # Database schema
│       ├── db/migration/   # Flyway migrations
│       └── functions/      # PostgreSQL functions
├── discovery-server/       # Service discovery (Port 8761)
│   └── src/main/java/com/bytebandit/discovery/
├── config-server/          # Configuration management (Port 8071)
│   └── src/main/java/com/bytebandit/configserver/
└── common/                 # Shared libraries and utilities
    └── core/               # Core domain models and interfaces
```

### Backend Configuration

- **`pom.xml`**: Parent Maven configuration with dependency management
- **`checkstyle.xml`**: Code style enforcement rules
- **`checkstyle-suppressions.xml`**: Exceptions to checkstyle rules

## Frontend Application (`apps/web/`)

The frontend is a **Next.js 14** application with TypeScript and Tailwind CSS:

```
web/
├── src/
│   ├── app/                # Next.js App Router
│   │   ├── (auth)/         # Authentication routes
│   │   │   ├── login/      # Login page
│   │   │   ├── register/   # Registration page
│   │   │   └── verify-email/ # Email verification
│   │   ├── app/            # Protected application routes
│   │   │   ├── my-files/   # File management
│   │   │   ├── shared-with-me/ # Shared files
│   │   │   ├── starred/    # Starred items
│   │   │   ├── trash/      # Deleted items
│   │   │   └── storage/    # Storage usage
│   │   └── components/     # Shared UI components
│   ├── components/         # Reusable UI components
│   │   ├── ui/             # Base UI components (shadcn/ui)
│   │   └── dialogForms/    # Form dialogs
│   ├── hooks/              # Custom React hooks
│   ├── lib/                # Utility libraries
│   ├── store/              # State management (Zustand)
│   ├── types/              # TypeScript type definitions
│   └── utils/              # Helper functions
├── public/                 # Static assets
├── tailwind.config.ts      # Tailwind CSS configuration
├── tsconfig.json           # TypeScript configuration
├── eslint.config.mjs       # ESLint configuration
└── package.json            # Node.js dependencies
```

## Documentation (`docs/`)

Comprehensive project documentation using **MkDocs**:

```
docs/
├── mkdocs/
│   ├── api/                # API documentation
│   │   ├── reference/      # Service API references
│   │   └── swagger.md      # Swagger/OpenAPI info
│   ├── developers/         # Developer guides
│   │   ├── code-style.md   # Coding standards
│   │   ├── branching-strategy.md # Git workflow
│   │   ├── project-structure.md # This file
│   │   ├── before-you-start.md # Getting started
│   │   └── starting-development.md # Development setup
│   ├── documentation/      # System documentation
│   │   ├── design/         # Architecture and design
│   │   ├── requirements/   # Functional requirements
│   │   └── security/       # Security documentation
│   ├── get-started/        # User guides
│   │   ├── local-setup.md  # Local development setup
│   │   └── usage.md        # Application usage
│   └── mkdocs.yml          # MkDocs configuration
```

## Infrastructure and Configuration

### Docker Configuration

- **`docker-compose.apps.yml`**: Main application services
- **`docker-compose.test.yaml`**: Test environment setup
- **`Dockerfile`**: Individual service containerization

### Service Management

- **`run-services.sh`**: Comprehensive service management script
- **`build-services.sh`**: Service building and deployment
- **`setup-test-env.sh`**: Test environment setup

### Development Tools

- **`.github/workflows/`**: CI/CD pipelines
- **`.idea/`**: IntelliJ IDEA configuration
- **`.gitignore`**: Git ignore patterns
- **`.markdownlint.json`**: Markdown linting rules

## Key Design Principles

### 1. **Separation of Concerns**
- Backend services are independent and focused
- Frontend components are modular and reusable
- Configuration is externalized and environment-specific

### 2. **Microservices Architecture**
- Each service has its own database and configuration
- Services communicate via REST APIs
- Service discovery and configuration management

### 3. **Monorepo Benefits**
- Shared dependencies and configurations
- Consistent development environment
- Unified documentation and tooling

### 4. **Configuration Management**
- Environment-specific configurations
- Centralized configuration server
- Docker-based deployment

## File Naming Conventions

- **Controllers**: `*Controller.java` (e.g., `UserLoginController.java`)
- **Services**: `*Service.java` (e.g., `UserService.java`)
- **Repositories**: `*Repository.java` (e.g., `UserRepository.java`)
- **Models**: Descriptive names (e.g., `AuthenticatedUser.java`)
- **Components**: PascalCase (e.g., `FileActions.tsx`)
- **Hooks**: `use*` prefix (e.g., `useSession.ts`)

## Database Structure

Each service maintains its own database schema:
- **User Service**: User accounts, authentication, profiles
- **File Service**: File system items, sharing, permissions
- **Gateway**: Session management, routing rules

## Getting Started

1. **Clone the repository**: `git clone <repository-url>`
2. **Navigate to backend**: `cd apps/backend`
3. **Build services**: `mvn clean install`
4. **Navigate to frontend**: `cd apps/web`
5. **Install dependencies**: `npm install`
6. **Start services**: `./run-services.sh start all`

For detailed setup instructions, see the [Local Setup Guide](../get-started/local-setup.md).
