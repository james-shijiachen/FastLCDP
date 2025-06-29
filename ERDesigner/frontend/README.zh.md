# FastLCDP 前端

**中文** | [English](README.md)

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.0+-green.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-4.0+-purple.svg)](https://vitejs.dev/)
[![node.js](https://img.shields.io/badge/node.js-18.0+-green.svg)](https://nodejs.org/)
[![npm](https://img.shields.io/badge/npm-8.0+-orange.svg)](https://www.npmjs.com/)

## 项目概述

ERDesigner前端是一个基于Vue 3和TypeScript的现代化ER图设计工具，支持实体关系建模、可视化编辑和多种导出格式。它为数据库设计师和开发者提供了直观的界面来可视化地创建和管理数据库模式。

## ✨ 特性

- 🎨 **直观的可视化界面** - 拖拽式操作，所见即所得
- 🔧 **实体管理** - 支持实体创建、编辑、删除和字段管理
- 🔗 **关系建模** - 支持一对一、一对多、多对多关系
- 📏 **尺寸调整** - 实体框支持拖拽调整大小
- 💾 **数据持久化** - 本地存储，支持项目保存和加载
- 📤 **多格式导出** - 支持JSON、SQL、图片等格式导出
- 🌙 **主题切换** - 支持明暗主题切换
- 📱 **响应式设计** - 适配不同屏幕尺寸

## 🚀 快速开始

### 环境要求

- Node.js >= 18.0.0
- npm >= 8.0.0

### 本地开发

```bash
# 克隆项目
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP/ERDesigner/frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 在浏览器中打开 http://localhost:3001
```

### 构建生产版本

```bash
# 构建项目
npm run build

# 预览构建结果
npm run preview
```

## 📁 项目结构

```
FastLCDP-Frontend/
├── src/
│   ├── components/          # Vue组件
│   │   ├── ERCanvas.vue    # ER图画布组件
│   │   ├── EntityPanel.vue # 实体面板组件
│   │   └── ...
│   ├── stores/             # Pinia状态管理
│   │   └── erDiagram.ts    # ER图状态管理
│   ├── types/              # TypeScript类型定义
│   │   ├── entity.ts       # 实体类型
│   │   └── ...
│   ├── utils/              # 工具函数
│   ├── styles/             # 样式文件
│   └── App.vue             # 根组件
├── public/                 # 静态资源
├── docker-compose.yml      # Docker Compose配置
├── Dockerfile              # Docker配置
├── nginx.conf              # Nginx配置
└── package.json            # 项目配置
```

## 🛠️ 技术栈

- **前端框架**: Vue 3
- **开发语言**: TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **UI组件**: Element Plus
- **图标**: Element Plus Icons
- **样式**: CSS3 + Flexbox
- **容器化**: Docker + Docker Compose
- **Web服务器**: Nginx

## 📝 环境配置

复制 `.env.example` 文件为 `.env` 并根据需要修改配置：

```bash
cp .env.example .env
```

主要配置项：

- `VITE_APP_TITLE`: 应用标题
- `VITE_API_BASE_URL`: API基础URL
- `VITE_DEV_SERVER_PORT`: 开发服务器端口
- `VITE_DEFAULT_THEME`: 默认主题

## 🎯 使用指南

### 创建实体

1. 从左侧工具栏拖拽实体到画布
2. 双击实体进行编辑
3. 添加字段和设置属性

### 建立关系

1. 选择源实体
2. 拖拽到目标实体
3. 选择关系类型

### 调整尺寸

1. 选中实体
2. 拖拽四角的控制点调整大小

### 导出项目

1. 点击导出按钮
2. 选择导出格式（JSON/SQL/PNG）
3. 下载文件

## 📄 许可证

本项目采用 Apache 2.0 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🙏 致谢

- [Vue.js](https://vuejs.org/) - 渐进式JavaScript框架
- [Element Plus](https://element-plus.org/) - Vue 3组件库
- [Vite](https://vitejs.dev/) - 下一代前端构建工具
- [Pinia](https://pinia.vuejs.org/) - Vue状态管理库

---

⭐ 如果这个项目对你有帮助，请给它一个星标！