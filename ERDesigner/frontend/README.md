# FastLCDP Frontend

[中文](README_zh.md) | **English**

[![Vue](https://img.shields.io/badge/Vue-3.0+-green.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-4.0+-purple.svg)](https://vitejs.dev/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)

## Overview

ERDesigner Frontend is a modern ER diagram design tool based on Vue 3 and TypeScript, supporting entity-relationship modeling, visual editing, and multiple export formats. It provides an intuitive interface for database designers and developers to create and manage database schemas visually.

## ✨ Features

- 🎨 **Intuitive Visual Interface** - Drag-and-drop operations, WYSIWYG
- 🔧 **Entity Management** - Support for entity creation, editing, deletion, and field management
- 🔗 **Relationship Modeling** - Support for one-to-one, one-to-many, many-to-many relationships
- 📏 **Size Adjustment** - Entity boxes support drag-to-resize
- 💾 **Data Persistence** - Local storage with project save and load support
- 📤 **Multi-format Export** - Support for JSON, SQL, image exports
- 🌙 **Theme Switching** - Support for light and dark theme switching
- 📱 **Responsive Design** - Adapts to different screen sizes

## 🚀 Quick Start

### Requirements

- Node.js >= 18.0.0
- npm >= 8.0.0

### Local Development

```bash
# Clone the project
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP/ERDesigner/frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Open http://localhost:3001 in your browser
```

### Build for Production

```bash
# Build the project
npm run build

# Preview the build
npm run preview
```

## 🐳 Docker Deployment

### Using Docker Compose (Recommended)

```bash
# Production deployment
cd docker && docker-compose up -d

# Development environment
cd docker && docker-compose -f docker-compose.dev.yml up -d
```

### Using Docker

```bash
# Build image
docker build -f docker/Dockerfile -t fastlcdp-frontend .

# Run container
docker run -d -p 3000:8080 --name fastlcdp-frontend fastlcdp-frontend
```

## 📁 Project Structure

```
FastLCDP-Frontend/
├── src/
│   ├── components/          # Vue components
│   │   ├── ERCanvas.vue    # ER diagram canvas component
│   │   ├── EntityPanel.vue # Entity panel component
│   │   └── ...
│   ├── stores/             # Pinia state management
│   │   └── erDiagram.ts    # ER diagram state management
│   ├── types/              # TypeScript type definitions
│   │   ├── entity.ts       # Entity types
│   │   └── ...
│   ├── utils/              # Utility functions
│   ├── styles/             # Style files
│   └── App.vue             # Root component
├── public/                 # Static assets
├── docker-compose.yml      # Docker Compose configuration
├── Dockerfile              # Docker configuration
├── nginx.conf              # Nginx configuration
└── package.json            # Project configuration
```

## 🛠️ Tech Stack

- **Frontend Framework**: Vue 3
- **Development Language**: TypeScript
- **Build Tool**: Vite
- **State Management**: Pinia
- **UI Components**: Element Plus
- **Icons**: Element Plus Icons
- **Styling**: CSS3 + Flexbox
- **Containerization**: Docker + Docker Compose
- **Web Server**: Nginx

## 📝 Environment Configuration

Copy `.env.example` to `.env` and modify as needed:

```bash
cp .env.example .env
```

Main configuration options:

- `VITE_APP_TITLE`: Application title
- `VITE_API_BASE_URL`: API base URL
- `VITE_DEV_SERVER_PORT`: Development server port
- `VITE_DEFAULT_THEME`: Default theme

## 🎯 Usage Guide

### Creating Entities

1. Drag an entity from the left toolbar to the canvas
2. Double-click the entity to edit
3. Add fields and set properties

### Establishing Relationships

1. Select the source entity
2. Drag to the target entity
3. Choose the relationship type

### Resizing

1. Select an entity
2. Drag the corner control points to resize

### Exporting Projects

1. Click the export button
2. Choose export format (JSON/SQL/PNG)
3. Download the file

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the Apache 2.0 License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [Vue.js](https://vuejs.org/) - Progressive JavaScript framework
- [Element Plus](https://element-plus.org/) - Vue 3 component library
- [Vite](https://vitejs.dev/) - Next generation frontend build tool
- [Pinia](https://pinia.vuejs.org/) - Vue state management library

## 📞 Contact

If you have any questions or suggestions, please contact us through:

- Submit an [Issue](https://github.com/your-username/FastLCDP-Frontend/issues)
- Send email to: shijiachen@traninfo.com.cn

---

⭐ If this project helps you, please give it a star!