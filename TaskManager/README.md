# TaskManager - Task Management System

[中文](README.zh.md) | **English**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.0+-green.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![node.js](https://img.shields.io/badge/node.js-18.0+-green.svg)](https://nodejs.org/)
[![npm](https://img.shields.io/badge/npm-8.0+-orange.svg)](https://www.npmjs.com/)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-orange.svg)](https://maven.apache.org/)

## Overview

TaskManager is a modern task management and collaboration platform that helps teams and individuals efficiently organize, track, and complete tasks. Built with Vue 3 and TypeScript for the frontend and Spring Boot with Java 21 for the backend, it provides a comprehensive solution for project management and team collaboration.

## ✨ Key Features

### Frontend Features
- 🎯 **Smart Task Management** - Create, assign, track tasks with priority and deadline support
- 👥 **Team Collaboration** - Multi-user collaboration with real-time task status synchronization
- 📊 **Project Kanban** - Visual kanban board with drag-and-drop task status management
- 📅 **Schedule Planning** - Integrated calendar view for optimal task time arrangement
- 🔔 **Smart Notifications** - Automatic reminders and notifications to ensure timely completion
- 📈 **Progress Tracking** - Real-time project and task progress tracking with statistical reports
- 🏷️ **Tag Classification** - Flexible tagging system for easy task categorization and filtering
- 💬 **Comments & Discussion** - Task commenting and discussion features to enhance team communication
- 📎 **File Attachments** - Support for file uploads and attachment management
- 🔍 **Advanced Search** - Powerful search and filtering capabilities
- 📱 **Responsive Design** - Optimized for desktop and mobile devices
- 🌙 **Theme Support** - Light and dark theme switching
- ⚡ **Real-time Sync** - Real-time data synchronization for seamless multi-device collaboration

### Backend Features
- 🔐 **Permission Management** - Fine-grained permission control for data security
- 📊 **Data Analytics** - Task completion rates, work efficiency, and other data analysis
- 🔄 **Workflow Integration** - Seamless integration with ProcessEngineer
- 🔌 **REST API** - Comprehensive API for integration with other systems
- 💾 **Data Persistence** - Reliable data storage and backup
- 🚀 **Performance Optimization** - High-performance caching and query optimization

## 🏗️ Architecture

### Frontend Architecture
- **Framework**: Vue 3 with Composition API
- **Language**: TypeScript for type safety
- **State Management**: Pinia for reactive state management
- **UI Components**: Element Plus + Custom components
- **Chart Rendering**: ECharts for data visualization
- **Build Tool**: Vite for fast development and building

### Backend Architecture
- **Framework**: Spring Boot 3.3+
- **Language**: Java 21 with modern features
- **Database**: Support for multiple database types
- **ORM**: MyBatis Plus for database operations
- **Cache**: Redis for high-performance caching
- **Message Queue**: RabbitMQ for asynchronous message processing
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Security**: Spring Security for authentication and authorization

## 🚀 Quick Start

### Prerequisites
- **Frontend**: Node.js >= 18.0.0, npm >= 8.0.0
- **Backend**: Java 21+, Maven 3.6+
- **Database**: H2 (default), MySQL, PostgreSQL, Oracle, or SQL Server
- **Cache**: Redis (optional)
- **Message Queue**: RabbitMQ (optional)

### Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP/TaskManager
```

#### 2. Backend Setup
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
The backend will start on http://localhost:8080

#### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
The frontend will start on http://localhost:3003

### Using Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 Usage Guide

### Getting Started with Task Management

1. **Create Projects**
   - Click the "New Project" button
   - Set project name, description, and members
   - Configure project permissions and settings

2. **Manage Tasks**
   - Create tasks within projects
   - Set task priority, deadline, and assignee
   - Add task descriptions and attachments

3. **Use Kanban View**
   - Switch to kanban view
   - Drag tasks between different status columns
   - Customize kanban columns and workflows

4. **Team Collaboration**
   - Invite team members to join projects
   - Add comments and discussions to tasks
   - Use @mention feature to notify relevant people

5. **Track Progress**
   - View project dashboard
   - Analyze task completion status
   - Generate progress reports

## 🔧 Configuration

### Backend Configuration
Edit `application.yaml` in the backend directory:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/taskmanager
    username: sa
    password: 
  
  redis:
    host: localhost
    port: 6379
    
task:
  notification:
    enabled: true
    email: true
    webhook: true
```

### Frontend Configuration
Edit `vite.config.ts` for build and development settings:

```typescript
export default defineConfig({
  server: {
    port: 3003,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

## 🗺️ Roadmap

- [ ] Gantt chart view
- [ ] Time tracking functionality
- [ ] Automation rules engine
- [ ] Native mobile applications
- [ ] Third-party integrations (Slack, DingTalk, etc.)
- [ ] AI-powered task recommendations
- [ ] Advanced reporting and analytics
- [ ] Multi-language internationalization

---

**TaskManager** is part of the [FastLCDP](../README.md) platform - A comprehensive low-code development ecosystem.