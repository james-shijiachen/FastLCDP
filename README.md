# FastLCDP - Fast Low-Code Development Platform

[中文](README.zh.md) | **English**

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

---

## 🚀 Project Overview

**FastLCDP** is an AI-powered low-code development platform. It enables AI to participate in the software development process like a human, accelerating delivery through the synergy of humans and AI. The platform consists of three core modules:

- [**ERDesigner**](./ERDesigner/README.md): Modern ER diagram design tool with visual modeling and AI-powered editing.
- [**ProcessEngineer**](./ProcessEngineer/README.md): Visual business process designer supporting BPMN standards and mainstream BPM engines.
- [**TaskManager**](./TaskManager/README.md): Smart task management system with Kanban, team collaboration, and real-time sync.

---

## 🛠️ Tech Stack
- **Backend**: Java 21, Spring Boot 3.5+, MyBatis Plus, Undertow, Log4j2, Spring Security, OpenAPI, JAXB, Lombok
- **Frontend**: Vue 3, TypeScript, Vite, Pinia, vue-i18n, Fabric.js
- **Database**: MySQL, PostgreSQL, Oracle, SQL Server, H2
- **Containerization**: Docker, Docker Compose
- **AI Platform**: Claude-3.7 Sonnet, Gemini 2.5 Pro, GPT-4, DeepSeek V3, Qwen 2.5-Max

---

## 📁 Project Structure
```
FastLCDP/
├── ERDesigner/              # ER Diagram Design Tool
│   ├── backend/             # Spring Boot Backend
│   └── frontend/            # Vue 3 Frontend
├── ProcessEngineer/         # Business Process Designer
│   ├── backend/             # Spring Boot Backend
│   └── frontend/            # Vue 3 Frontend
├── TaskManager/             # Task Management System
│   ├── backend/             # Spring Boot Backend
│   └── frontend/            # Vue 3 Frontend
├── docker/                  # Docker Configuration
├── docs/                    # Documentation
└── rules/                   # Coding Rules & Cursor AI Rules
```

---

## 📦 Development Standards
- Unified code style, naming, annotation, and error handling
- RESTful API design, internationalization, and security best practices
- [Detailed rules and best practices](rules/cursor/personal-rules.md)

---

## ⚡ Quick Start

### Using Docker (Recommended)
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP
cd docker
docker-compose up -d
```
- ERDesigner Frontend: http://localhost:3001/ERDesigner
- ERDesigner Backend API: http://localhost:8080/ERDesigner/api
- API Docs: http://localhost:8080/ERDesigner/swagger-ui.html

### Manual Setup
#### ERDesigner Backend
```bash
cd ERDesigner/backend
mvn clean install
mvn spring-boot:run
```
#### ERDesigner Frontend
```bash
cd ERDesigner/frontend
npm install
npm run dev
```

---

## 🧑‍💻 Common Commands
- Backend: `mvn clean compile`, `mvn test`, `mvn spring-boot:run`, `mvn clean package`
- Frontend: `npm install`, `npm run dev`, `npm run build`, `npm run preview`
- Docker: `docker build -t fastlcdp .`, `docker-compose up -d`, `docker-compose logs -f`

---

## 🤝 Contributing
1. Fork the project
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m 'feat: add your feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

**Contribution Guidelines:**
- Follow [personal-rules.md](rules/cursor/personal-rules.md) for code style and commit message conventions
- Add unit tests for new features
- Ensure code passes all checks before submitting PR

---

## 📄 License
This project is licensed under the Apache License 2.0 - see the [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE) file for details.

## 📬 Contact
- Project Homepage: [https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- Issue Reports: [https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- Email: [shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP Team** - Embrace AI, embrace tomorrow's dawn! 🚀