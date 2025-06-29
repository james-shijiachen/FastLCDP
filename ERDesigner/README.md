# ERDesigner

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

ERDesigner is a modern, intuitive ER (Entity-Relationship) diagram design tool that enables developers and database designers to create, edit, and manage database schemas visually. Built with Vue 3 and TypeScript for the frontend and Spring Boot with Java 21 for the backend, it provides a comprehensive solution for database design and modeling.

## ✨ Key Features

### Frontend Features
- 🎨 **Intuitive Visual Interface** - Drag-and-drop operations with WYSIWYG editing
- 🔧 **Entity Management** - Create, edit, delete entities and manage fields with ease
- 🔗 **Relationship Modeling** - Support for one-to-one, one-to-many, and many-to-many relationships
- 📏 **Flexible Sizing** - Drag-to-resize entity boxes for better layout control
- 💾 **Data Persistence** - Local storage with project save and load capabilities
- 📤 **Multi-format Export** - Export to JSON, SQL, PNG, and other formats
- 🌙 **Theme Support** - Light and dark theme switching
- 📱 **Responsive Design** - Optimized for different screen sizes and devices
- 🔍 **Zoom & Pan** - Navigate large diagrams with zoom and pan controls
- ⚡ **Real-time Collaboration** - Share and collaborate on designs in real-time

### Backend Features
- 🗄️ **Database Schema Management** - Store and manage ER diagram metadata
- 🔄 **DDL Generation** - Generate SQL DDL statements from ER diagrams
- 🌐 **Multi-database Support** - Support for MySQL, PostgreSQL, Oracle, SQL Server, and H2
- 📊 **Version Control** - Track changes and maintain diagram versions
- 🔌 **REST API** - Comprehensive API for integration with other tools
- 🛡️ **Data Validation** - Validate entity relationships and constraints
- 📈 **Analytics** - Usage statistics and diagram complexity analysis

## 🏗️ Architecture

### Frontend Architecture
- **Framework**: Vue 3 with Composition API
- **Language**: TypeScript for type safety
- **State Management**: Pinia for reactive state management
- **UI Components**: Custom components with modern design
- **Canvas Rendering**: SVG-based diagram rendering
- **Build Tool**: Vite for fast development and building

### Backend Architecture
- **Framework**: Spring Boot 3.3+
- **Language**: Java 21 with modern features
- **Database**: Support for multiple database types
- **ORM**: MyBatis Plus for database operations
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
cd FastLCDP/ERDesigner
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
The frontend will start on http://localhost:3001

### Using Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 Usage Guide

### Creating Your First ER Diagram

1. **Create Entities**
   - Click the "Add Entity" button
   - Define entity name and properties
   - Add fields with data types and constraints

2. **Define Relationships**
   - Select two entities
   - Choose relationship type (1:1, 1:N, N:M)
   - Configure foreign key constraints

3. **Customize Layout**
   - Drag entities to arrange layout
   - Resize entity boxes as needed
   - Use zoom and pan for navigation

4. **Export Your Design**
   - Export as SQL DDL statements
   - Save as JSON for later editing
   - Export as PNG image for documentation

## 🔧 Configuration

### Backend Configuration
Edit `application.yaml` in the backend directory:

```yaml
spring:
  datasource:
    url: jdbc:h2:file:./data/erdesigner
    username: sa
    password: 
  
database:
  type: H2
  auto-create-metadata-tables: true
```

### Frontend Configuration
Edit `vite.config.ts` for build and development settings:

```typescript
export default defineConfig({
  server: {
    port: 3001,
    proxy: {
      '/api': 'http://localhost:8080'
    }
  }
})
```

## 🗺️ Roadmap

- [ ] Real-time collaboration features
- [ ] Advanced export formats (PDF, SVG)
- [ ] Database reverse engineering
- [ ] Plugin system for custom extensions
- [ ] Cloud storage integration
- [ ] Mobile app support

---

**ERDesigner** is part of the [FastLCDP](../README.md) platform - A comprehensive low-code development ecosystem.