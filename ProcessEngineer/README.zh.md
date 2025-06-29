# ProcessEngineer - 流程设计器

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

ProcessEngineer是一个现代化的业务流程设计和管理工具，帮助企业和开发者可视化地创建、编辑和管理业务流程。前端采用Vue 3和TypeScript构建，后端使用Spring Boot和Java 21，为业务流程建模和自动化提供了完整的解决方案。

## 核心功能

🎨 **直观的流程设计界面** - 拖拽式操作，所见即所得的流程编辑体验
🔧 **节点管理** - 轻松创建、编辑、删除流程节点并配置属性
🔗 **流程连接** - 支持顺序流、条件流、并行流等多种连接类型
📏 **灵活布局** - 拖拽调整节点位置，优化流程图布局
💾 **流程持久化** - 本地存储，支持流程保存和加载
📤 **多格式导出** - 导出为BPMN、JSON、PNG等多种格式
🌙 **主题支持** - 明暗主题切换
📱 **响应式设计** - 适配不同屏幕尺寸和设备
🔍 **缩放平移** - 缩放和平移控制，轻松导航复杂流程图
⚡ **实时协作** - 实时分享和协作设计
🌐 **多引擎支持** - 支持Activiti、Flowable、Camunda等流程引擎
🗄️ **流程版本管理** - 存储和管理流程定义版本
🔄 **流程部署** - 一键部署流程到执行引擎
🛡️ **流程验证** - 验证流程定义的正确性和完整性
📈 **流程分析** - 智能分析流程设计的问题和优化建议
📊 **执行监控** - 实时监控流程执行状态和性能

## 🏗️ 技术架构

### 前端架构
- **框架**: Vue 3 with Composition API
- **语言**: TypeScript 提供类型安全
- **状态管理**: Pinia 响应式状态管理
- **UI组件**: 现代化设计的自定义组件
- **流程渲染**: 基于SVG的流程图渲染
- **构建工具**: Vite 快速开发和构建

### 后端架构
- **框架**: Spring Boot 3.3+
- **语言**: Java 21 现代特性
- **数据库**: 支持多种数据库类型
- **ORM**: MyBatis Plus 数据库操作
- **流程引擎**: 集成主流BPM引擎
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
cd FastLCDP/ProcessEngineer
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
前端将在 http://localhost:3002 启动

### 使用Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 使用指南

### 创建您的第一个业务流程

1. **创建流程节点**
   - 点击"添加节点"按钮
   - 选择节点类型（开始、任务、网关、结束等）
   - 配置节点属性和参数

2. **连接流程节点**
   - 选择源节点和目标节点
   - 选择连接类型（顺序流、条件流等）
   - 配置流转条件

3. **自定义流程布局**
   - 拖拽节点来安排布局
   - 根据需要调整连接线路径
   - 使用缩放和平移进行导航

4. **部署和执行**
   - 验证流程定义
   - 部署到流程引擎
   - 启动流程实例

## 🔧 配置说明

### 后端配置
编辑后端目录中的 `application.yaml`：

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

### 前端配置
编辑 `vite.config.ts` 进行构建和开发设置：

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

## 🗺️ 发展路线图

- [ ] 高级流程模板库
- [ ] 流程性能优化建议
- [ ] 移动端流程审批
- [ ] 流程挖掘和分析
- [ ] 智能流程推荐
- [ ] 多租户支持
- [ ] 流程API网关

---

**ProcessEngineer** 是 [FastLCDP](../README.md) 平台的一部分 - 一个综合性的低代码开发生态系统。