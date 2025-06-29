# ER Designer

**中文** | [English](README.md)

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![Vue](https://img.shields.io/badge/Vue-3.0+-green.svg)](https://vuejs.org/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.0+-blue.svg)](https://www.typescriptlang.org/)
[![node.js](https://img.shields.io/badge/node.js-18.0+-green.svg)](https://nodejs.org/)
[![npm](https://img.shields.io/badge/npm-8.0+-orange.svg)](https://www.npmjs.com/)
[![Java](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3+-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-orange.svg)](https://maven.apache.org/)

## 项目概述

ERDesigner是一个现代化、直观的ER（实体关系）图设计工具，帮助开发者和数据库设计师可视化地创建、编辑和管理数据库模式。前端采用Vue 3和TypeScript构建，后端使用Spring Boot和Java 21，为数据库设计和建模提供了完整的解决方案。

## 核心功能

🎨 **直观的可视化界面** - 拖拽式操作，所见即所得的编辑体验
🔧 **实体管理** - 轻松创建、编辑、删除实体并管理字段
🔗 **关系建模** - 支持一对一、一对多、多对多关系
📏 **灵活调整** - 拖拽调整实体框大小，优化布局控制
💾 **数据持久化** - 本地存储，支持项目保存和加载
📤 **多格式导出** - 导出为JSON、SQL、PNG等多种格式
🌙 **主题支持** - 明暗主题切换
📱 **响应式设计** - 适配不同屏幕尺寸和设备
🔍 **缩放平移** - 缩放和平移控制，轻松导航大型图表
⚡ **实时协作** - 实时分享和协作设计
🌐 **多数据库支持** - 支持MySQL、PostgreSQL、Oracle、SQL Server和H2
🗄️ **数据库模式管理** - 存储和管理ER图元数据
🔄 **DDL生成** - 从ER图生成SQL DDL语句
🛡️ **数据验证** - 验证实体关系和约束
📈 **分析统计** - 智能分析设计的问题和风险
📊 **版本控制** - 跟踪变更并维护图表版本

## 🏗️ 技术架构

### 前端架构
- **框架**: Vue 3 with Composition API
- **语言**: TypeScript 提供类型安全
- **状态管理**: Pinia 响应式状态管理
- **UI组件**: 现代化设计的自定义组件
- **画布渲染**: 基于SVG的图表渲染
- **构建工具**: Vite 快速开发和构建

### 后端架构
- **框架**: Spring Boot 3.3+
- **语言**: Java 21 现代特性
- **数据库**: 支持多种数据库类型
- **ORM**: MyBatis Plus 数据库操作
- **API文档**: OpenAPI 3.0 with Swagger UI
- **安全**: Spring Security 认证和授权

## 🚀 快速开始

### 环境要求
- **前端**: Node.js >= 18.0.0, npm >= 8.0.0
- **后端**: Java 21+, Maven 3.6+
- **数据库**: H2（默认）、MySQL、PostgreSQL、Oracle或SQL Server

### 开发环境搭建

#### 1. 克隆仓库
```bash
git clone https://github.com/james-shijiachen/fastLCDP.git
cd FastLCDP/ERDesigner
```

#### 2. 后端设置
```bash
cd backend
mvn clean install
mvn spring-boot:run
```
后端将在 http://localhost:8080 启动

#### 3. 前端设置
```bash
cd frontend
npm install
npm run dev
```
前端将在 http://localhost:3001 启动

### 使用Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 使用指南

### 创建您的第一个ER图

1. **创建实体**
   - 点击"添加实体"按钮
   - 定义实体名称和属性
   - 添加字段及数据类型和约束

2. **定义关系**
   - 选择两个实体
   - 选择关系类型（1:1、1:N、N:M）
   - 配置外键约束

3. **自定义布局**
   - 拖拽实体来安排布局
   - 根据需要调整实体框大小
   - 使用缩放和平移进行导航

4. **导出设计**
   - 导出为SQL DDL语句
   - 保存为JSON以便后续编辑
   - 导出为PNG图片用于文档

## 🔧 配置说明

### 后端配置
编辑后端目录中的 `application.yaml`：

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

### 前端配置
编辑 `vite.config.ts` 进行构建和开发设置：

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

## 🗺️ 发展路线图

- [ ] 实时协作功能
- [ ] 高级导出格式（PDF、SVG）
- [ ] 数据库逆向工程
- [ ] 自定义扩展插件系统
- [ ] 云存储集成
- [ ] 移动应用支持

---

**ERDesigner** 是 [FastLCDP](../README.md) 平台的一部分 - 一个综合性的低代码开发生态系统。