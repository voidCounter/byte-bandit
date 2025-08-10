# Before You Start

Welcome to Byte Bandit! This guide will help you understand what you need to know and have before contributing to the project.

## Prerequisites

### Required Knowledge

Before contributing to Byte Bandit, you should have a solid understanding of:

- **Java 17+**: Core Java concepts, Spring Boot framework
- **Spring Cloud**: Microservices architecture, service discovery, configuration management
- **RESTful APIs**: HTTP methods, status codes, request/response patterns
- **Git**: Basic Git operations, branching, pull requests
- **Docker**: Container concepts, Docker Compose
- **PostgreSQL**: Basic SQL, database design principles
- **TypeScript/React**: Frontend development with Next.js
- **Microservices**: Distributed systems, service communication

### Required Tools

Ensure you have the following tools installed:

- **Java Development Kit (JDK) 17 or higher**
- **Maven 3.6+** for backend development
- **Node.js 18+** and npm for frontend development
- **Docker and Docker Compose** for local development
- **Git** for version control
- **IntelliJ IDEA** (recommended) or VS Code for development

## Development Environment Setup

### 1. Java Environment

```bash
# Check Java version
java -version

# Should show Java 17 or higher
# If not, install OpenJDK 17
```

**Recommended**: Use IntelliJ IDEA with the CheckStyle plugin for consistent code quality.

### 2. Node.js Environment

```bash
# Check Node.js version
node --version

# Should show v18 or higher
# If not, install Node.js 18+ from nodejs.org
```

### 3. Docker Setup

```bash
# Check Docker version
docker --version
docker-compose --version

# Ensure Docker daemon is running
docker ps
```

### 4. Database Setup

The project uses PostgreSQL for data persistence. Docker Compose will handle this automatically, but you can also:

- Install PostgreSQL locally (port 5432)
- Use Docker containers (recommended for development)
- Configure connection settings in your environment

## Project Architecture Overview

### Microservices Structure

Byte Bandit follows a microservices architecture with these core services:

- **API Gateway** (Port 8084): Entry point for all client requests
- **User Service** (Port 8083): User management and authentication
- **File Service** (Port 8081): File operations and storage
- **Discovery Server** (Port 8761): Service registration and discovery
- **Config Server** (Port 8071): Centralized configuration management

### Technology Stack

**Backend:**
- Spring Boot 3.x with Spring Cloud
- Spring Security for authentication
- Spring Data JPA for data access
- Flyway for database migrations
- Maven for dependency management

**Frontend:**
- Next.js 14 with App Router
- TypeScript for type safety
- Tailwind CSS for styling
- shadcn/ui for component library
- Zustand for state management

**Infrastructure:**
- Docker for containerization
- PostgreSQL for data persistence
- AWS S3 for file storage
- Kafka for event streaming
- Redis for caching (planned)

## Code Quality Standards

### Java Code Style

- **Checkstyle**: Enforces Google Java Style Guide with project-specific modifications
- **SonarQube**: Code quality analysis and security scanning
- **Javadoc**: Required for public methods and classes
- **Testing**: Minimum 80% code coverage required

### TypeScript/JavaScript Code Style

- **ESLint**: Code quality and style enforcement
- **Prettier**: Code formatting
- **TypeScript**: Strict type checking enabled
- **Testing**: Jest for unit testing

### Git Commit Standards

Follow conventional commit format:

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Test additions/changes
- `chore`: Maintenance tasks

**Example:**
```
feat(auth): implement JWT token refresh

- Add refresh token endpoint
- Implement token rotation
- Add unit tests for token service

Closes #123
```

## Development Workflow

### 1. **Fork and Clone**
- Fork the repository on GitHub
- Clone your fork locally
- Add upstream remote for syncing

### 2. **Branch Strategy**
- Create feature branches from `develop`
- Use descriptive branch names: `feature/user-authentication`
- Keep branches focused and small

### 3. **Development Process**
- Write tests first (TDD approach)
- Follow coding standards
- Run local tests before committing
- Update documentation as needed

### 4. **Pull Request Process**
- Create descriptive PR titles
- Include detailed descriptions
- Reference related issues
- Ensure all checks pass

## Common Development Tasks

### Adding New Features

1. **Create feature branch** from `develop`
2. **Implement feature** with tests
3. **Update documentation** if needed
4. **Run quality checks** locally
5. **Create pull request** to `develop`

### Fixing Bugs

1. **Create bugfix branch** from `develop`
2. **Write failing test** to reproduce the bug
3. **Fix the bug** and ensure tests pass
4. **Create pull request** to `develop`

### Code Reviews

- Review code for functionality and quality
- Check adherence to coding standards
- Verify test coverage
- Ensure documentation is updated

## Troubleshooting Common Issues

### Build Failures

```bash
# Clean and rebuild
mvn clean install

# Check Java version compatibility
java -version

# Verify Maven settings
mvn --version
```

### Docker Issues

```bash
# Clean Docker environment
docker system prune -a

# Rebuild images
docker-compose -f docker-compose.apps.yml build --no-cache

# Check service logs
docker-compose -f docker-compose.apps.yml logs [service-name]
```

### Database Connection Issues

- Verify PostgreSQL is running
- Check connection settings in `bootstrap.yaml`
- Ensure database migrations are applied
- Check service health endpoints

## Getting Help

### Documentation Resources

- **Project Structure**: [Project Structure Guide](project-structure.md)
- **Code Style**: [Code Style Guidelines](code-style.md)
- **Branching Strategy**: [Git Workflow](branching-strategy.md)
- **API Documentation**: [API Reference](../api/reference/)

### Communication Channels

- **GitHub Issues**: Report bugs and feature requests
- **Pull Requests**: Code review and discussion
- **Project Wiki**: Additional documentation and guides

## Next Steps

1. **Read the [Project Structure](project-structure.md)** to understand the codebase
2. **Follow the [Code Style Guidelines](code-style.md)** for consistent code
3. **Learn the [Branching Strategy](branching-strategy.md)** for Git workflow
4. **Set up your [Development Environment](starting-development.md)**
5. **Start with a small issue** to get familiar with the process
