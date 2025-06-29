# FastLCDP - Fast Low-Code Development Platform

[中文](README.zh.md) | **English**

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

## Project Overview

FastLCDP is an AI-powered low-code development platform. The core philosophy of the product is to enable AI to participate in the software project development process like humans, accelerating software project delivery through the perfect combination of humans and AI. The platform includes the following core products:

### [**ERDesigner**](./ERDesigner/README.md)
- **Modern ER Diagram Design Tool**: Provides a visual interface for entity-relationship modeling, supporting creation, editing, and deletion of entities and relationships.
- **Visual Editing Capabilities**: Based on AI-powered graph editing models, users can complete entity-relationship modeling without complex coding.

### [**ProcessEngineer**](./ProcessEngineer/README.md)
- **Business Process Designer**: Provides visual business process modeling tools with BPMN standard support for process design and management.
- **Process Engine Integration**: Supports mainstream BPM engines like Activiti, Flowable, and Camunda for process deployment and execution.

### [**TaskManager**](./TaskManager/README.md)
- **Smart Task Management System**: Provides project management and team collaboration features with task assignment, progress tracking, and team collaboration.
- **Visual Kanban Board**: Uses kanban view for task status management with drag-and-drop operations and real-time synchronization.

## 🚀 Quick Start

### Using Docker (Recommended)

1. **Clone the Repository**
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP
```

2. **Start with Docker**
```bash
cd docker
docker-compose up -d
```

3. **Access Applications**
- ERDesigner Frontend: http://localhost:3001/ERDesigner
- ERDesigner Backend API: http://localhost:8080/ERDesigner/api
- API Documentation: http://localhost:8080/ERDesigner/swagger-ui.html

### Manual Setup

1. ** ERDesigner Backend (Example) **
```bash
cd ERDesigner/backend
mvn clean install
mvn spring-boot:run
```

2. ** ERDesigner Frontend (Example) **
```bash
cd ERDesigner/frontend
npm install
npm run dev
```

## 📁 Project Structure

```
FastLCDP/
├── ERDesigner/              # ER Diagram Design Tool
│   ├── backend/             # Spring Boot Backend
│   └── frontend/            # Vue 3 Frontend
├── docker/                  # Docker Configuration
└── docs/                    # Documentation
```

## Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE) file for details.

## Contact

- Project Homepage: [https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- Issue Reports: [https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- Email: [shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP Team** - Embrace AI, embrace tomorrow's dawn! 🚀