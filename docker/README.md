# Docker Deployment Guide

[中文](./README.zh.md) | **English**

## Project Overview

This configuration provides complete Docker deployment solutions for the FastLCDP platform, supporting both single-project and multi-project deployment modes:

- **Single Project Mode**: Deploy ERDesigner independently
- **Multi-Project Mode**: Deploy ERDesigner, ProcessEngineer, and TaskManager together with unified access through ContextPath

### Multi-Project Access (Recommended)
- **Frontend Unified Entry**: http://localhost:3000
- **Backend Unified Entry**: http://localhost:8080
- **ERDesigner**: http://localhost:3000/erdesigner
- **ProcessEngineer**: http://localhost:3000/processengineer
- **TaskManager**: http://localhost:3000/taskmanager

## Quick Start

### Multi-Project Deployment (Recommended)

**Linux/macOS:**
```bash
# Execute in the docker directory
cd docker

# Start all projects with unified access
./start-multi-projects.sh
```

**Windows:**
```cmd
REM Execute in the docker directory
cd docker

REM Start all projects with unified access
start-multi-projects.bat
```

**Manual Start:**
```bash
cd docker
docker-compose -f docker-compose.multi.yml up --build -d
```

### Single Project Deployment (ERDesigner Only)

**Linux/macOS:**
```bash
# Execute in the docker directory
cd docker

# Start production environment
./start-erdesigner.sh start

# Start development environment
./start-erdesigner.sh dev

# Check service status
./start-erdesigner.sh status

# View logs
./start-erdesigner.sh logs

# Stop services
./start-erdesigner.sh stop
```

**Windows:**
```cmd
REM Execute in the docker directory
cd docker

REM Start production environment
start-erdesigner.bat start

REM Start development environment
start-erdesigner.bat dev

REM Check service status
start-erdesigner.bat status
```

### Method 2: Direct Docker Compose Usage

```bash
# Execute in the docker directory
cd docker

# Start production environment
docker-compose up -d

# Start development environment
docker-compose -f docker-compose.dev.yml up -d

# Check service status
docker-compose ps
```

### 1. Build and Start Application

```bash
# Build and start application
docker-compose up -d

# View logs
docker-compose logs -f fastlcdp-app
```

### 2. Access Application

- **ERDesigner Frontend**: http://localhost:3001
- **ERDesigner Backend API**: http://localhost:8080
- **API Documentation**: http://localhost:8080/swagger-ui.html
- **H2 Database Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/erdesigner`
  - Username: `sa`
  - Password: (leave empty)
- **Backend Health Check**: http://localhost:8080/actuator/health

### 3. Data Persistence

Database files are stored in the `./data` directory, which is mounted to the container to ensure data persistence.

### 4. Log Viewing

Application logs are stored in the `./logs` directory and can be viewed directly:

```bash
# View backend logs
docker-compose logs -f erdesigner-backend

# View frontend logs
docker-compose logs -f erdesigner-frontend

# View all service logs
docker-compose logs -f
```

### 5. Stop Application

```bash
# Stop all services
docker-compose down

# Stop services and remove data volumes
docker-compose down -v

# Rebuild and start
docker-compose up --build -d
```

## 🏗️ Service Architecture

### Service Components
- **erdesigner-frontend**: Vue 3 frontend application (Port: 3001)
- **erdesigner-backend**: Spring Boot backend API (Port: 8080)
- **erdesigner-network**: Internal network connecting frontend and backend services

### Data Persistence
- **Database Files**: `./data/` directory (H2 database files)
- **Application Logs**: `./logs/` directory

## 🔧 Configuration

### Environment Variables

#### Backend Environment Variables
- `SPRING_PROFILES_ACTIVE=docker` - Activate Docker configuration profile

#### Frontend Environment Variables
- `VITE_API_BASE_URL=http://localhost:8080` - Backend API address

### Custom Configuration

To modify configuration, edit the `docker-compose.yml` file:

```yaml
services:
  erdesigner-backend:
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_TYPE=MYSQL  # Switch to MySQL
      - DB_HOST=mysql
      - DB_PORT=3306
  
  erdesigner-frontend:
    environment:
      - VITE_API_BASE_URL=http://your-backend-url
```

## 🚀 Production Deployment

### Using External Database

1. **Add MySQL Service**
```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: erdesigner
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

2. **Update Backend Configuration**
```yaml
erdesigner-backend:
  environment:
    - SPRING_PROFILES_ACTIVE=prod
    - DB_TYPE=MYSQL
    - DB_HOST=mysql
    - DB_USERNAME=root
    - DB_PASSWORD=rootpassword
```

### Using Nginx Reverse Proxy

```yaml
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
    - "443:443"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
  depends_on:
    - erdesigner-frontend
    - erdesigner-backend
```

## 🛠️ Development Mode

### Local Development

If you only need to start the backend service for frontend development:

```bash
# Start backend only
docker-compose up erdesigner-backend -d

# Frontend local development
cd ../ERDesigner/frontend
npm install
npm run dev
```

### Hot Reload Development

Modify `docker-compose.yml` to add volume mounting:

```yaml
erdesigner-frontend:
  volumes:
    - ../ERDesigner/frontend/src:/app/src
  command: npm run dev
```

## 📊 Monitoring and Debugging

### Container Status Check
```bash
# Check container status
docker-compose ps

# Check container resource usage
docker stats

# Enter container for debugging
docker-compose exec erdesigner-backend bash
docker-compose exec erdesigner-frontend sh
```

### Network Debugging
```bash
# View networks
docker network ls
docker network inspect docker_erdesigner-network

# Test service connectivity
docker-compose exec erdesigner-frontend wget -qO- http://erdesigner-backend:8080/actuator/health
```

## Configuration Details

### Environment Variables

- `SPRING_PROFILES_ACTIVE=docker`: Use Docker configuration profile

### Data Volumes

- `./logs:/app/logs`: Log file persistence
- `./examples:/app/examples:ro`: Example files (read-only)
- `./data:/app/data`: Database file persistence

### Port Mapping

- `8080:8080`: Application service port

## Extended Configuration

If you need to connect to an external database, you can modify the datasource configuration in `application-docker.yaml` and add corresponding environment variables in `docker-compose.yml`.

### External MySQL Database Connection Example

Add environment variables in `docker-compose.yml`:

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/fastlcdp
  - SPRING_DATASOURCE_USERNAME=your-username
  - SPRING_DATASOURCE_PASSWORD=your-password
```

## Troubleshooting

### Application Startup Failure

1. Check logs: `docker-compose logs fastlcdp-app`
2. Ensure port 8080 is not occupied
3. Check data directory permissions

### Database Connection Issues

1. Ensure `./data` directory exists and has write permissions
2. Check if H2 database files are created properly

### Performance Optimization

1. Adjust JVM memory parameters as needed
2. Modify Hikari connection pool configuration
3. Enable Spring Boot lazy loading feature