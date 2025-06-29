# ProcessEngineer - Business Process Designer

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

ProcessEngineer is a modern business process design and management tool that enables enterprises and developers to create, edit, and manage business processes visually. Built with Vue 3 and TypeScript for the frontend and Spring Boot with Java 21 for the backend, it provides a comprehensive solution for business process modeling and automation.

## ✨ Key Features

### Frontend Features
- 🎨 **Intuitive Process Design Interface** - Drag-and-drop operations with WYSIWYG process editing
- 🔧 **Node Management** - Create, edit, delete process nodes and configure properties with ease
- 🔗 **Process Connections** - Support for sequence flows, conditional flows, parallel flows, and more
- 📏 **Flexible Layout** - Drag nodes to arrange layout and optimize process diagram structure
- 💾 **Process Persistence** - Local storage with process save and load capabilities
- 📤 **Multi-format Export** - Export to BPMN, JSON, PNG, and other formats
- 🌙 **Theme Support** - Light and dark theme switching
- 📱 **Responsive Design** - Optimized for different screen sizes and devices
- 🔍 **Zoom & Pan** - Navigate complex process diagrams with zoom and pan controls
- ⚡ **Real-time Collaboration** - Share and collaborate on process designs in real-time

### Backend Features
- 🗄️ **Process Version Management** - Store and manage process definition versions
- 🔄 **Process Deployment** - One-click deployment to process engines
- 🌐 **Multi-engine Support** - Support for Activiti, Flowable, Camunda, and other BPM engines
- 📊 **Execution Monitoring** - Real-time monitoring of process execution status and performance
- 🔌 **REST API** - Comprehensive API for integration with other systems
- 🛡️ **Process Validation** - Validate process definition correctness and completeness
- 📈 **Process Analytics** - Intelligent analysis of process design issues and optimization suggestions

## 🏗️ Architecture

### Frontend Architecture
- **Framework**: Vue 3 with Composition API
- **Language**: TypeScript for type safety
- **State Management**: Pinia for reactive state management
- **UI Components**: Custom components with modern design
- **Process Rendering**: SVG-based process diagram rendering
- **Build Tool**: Vite for fast development and building

### Backend Architecture
- **Framework**: Spring Boot 3.3+
- **Language**: Java 21 with modern features
- **Database**: Support for multiple database types
- **ORM**: MyBatis Plus for database operations
- **Process Engine**: Integration with mainstream BPM engines
- **API Documentation**: OpenAPI 3.0 with Swagger UI
- **Security**: Spring Security for authentication and authorization

## 🚀 Quick Start

### Prerequisites
- **Frontend**: Node.js >= 18.0.0, npm >= 8.0.0
- **Backend**: Java 21+, Maven 3.6+
- **Database**: H2 (default), MySQL, PostgreSQL, Oracle, or SQL Server

### Development Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP/ProcessEngineer
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
The frontend will start on http://localhost:3002

### Using Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 Usage Guide

### Creating Your First Business Process

1. **Create Process Nodes**
   - Click the "Add Node" button
   - Select node type (Start, Task, Gateway, End, etc.)
   - Configure node properties and parameters

2. **Connect Process Nodes**
   - Select source and target nodes
   - Choose connection type (sequence flow, conditional flow, etc.)
   - Configure flow conditions

3. **Customize Process Layout**
   - Drag nodes to arrange layout
   - Adjust connection paths as needed
   - Use zoom and pan for navigation

4. **Deploy and Execute**
   - Validate process definition
   - Deploy to process engine
   - Start process instances

## 🔧 Configuration

### Backend Configuration
Edit `application.yaml` in the backend directory:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/processengine
    username: sa
    password: 
  
process:
  engine:
    type: ACTIVITI
    auto-deploy: true
```

### Frontend Configuration
Edit `vite.config.ts` for build and development settings:

```typescript
export default defineConfig({
  server: {
    port: 3002,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

## 🗺️ Roadmap

- [ ] Advanced process template library
- [ ] Process performance optimization suggestions
- [ ] Mobile process approval
- [ ] Process mining and analytics
- [ ] Intelligent process recommendations
- [ ] Multi-tenant support
- [ ] Process API gateway

---

**ProcessEngineer** is part of the [FastLCDP](../README.md) platform - A comprehensive low-code development ecosystem.