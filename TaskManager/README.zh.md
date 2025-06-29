# TaskManager - 任务管理系统

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

TaskManager是一个现代化的任务管理和协作平台，帮助团队和个人高效地组织、跟踪和完成任务。前端采用Vue 3和TypeScript构建，后端使用Spring Boot和Java 21，为项目管理和团队协作提供了完整的解决方案。

## 核心功能

🎯 **智能任务管理** - 创建、分配、跟踪任务，支持优先级和截止日期
👥 **团队协作** - 多用户协作，实时同步任务状态和进度
📊 **项目看板** - 可视化看板视图，支持拖拽式任务状态管理
📅 **日程规划** - 集成日历视图，合理安排任务时间
🔔 **智能提醒** - 自动提醒和通知，确保任务按时完成
📈 **进度跟踪** - 实时跟踪项目和任务进度，生成统计报告
🏷️ **标签分类** - 灵活的标签系统，便于任务分类和筛选
💬 **评论讨论** - 任务评论和讨论功能，促进团队沟通
📎 **文件附件** - 支持文件上传和附件管理
🔍 **高级搜索** - 强大的搜索和过滤功能
📱 **响应式设计** - 适配桌面端和移动端设备
🌙 **主题支持** - 明暗主题切换
⚡ **实时同步** - 实时数据同步，多端协作无缝衔接
🔐 **权限管理** - 细粒度权限控制，保障数据安全
📊 **数据分析** - 任务完成率、工作效率等数据分析
🔄 **工作流集成** - 与ProcessEngineer无缝集成

## 🏗️ 技术架构

### 前端架构
- **框架**: Vue 3 with Composition API
- **语言**: TypeScript 提供类型安全
- **状态管理**: Pinia 响应式状态管理
- **UI组件**: Element Plus + 自定义组件
- **图表渲染**: ECharts 数据可视化
- **构建工具**: Vite 快速开发和构建

### 后端架构
- **框架**: Spring Boot 3.3+
- **语言**: Java 21 现代特性
- **数据库**: 支持多种数据库类型
- **ORM**: MyBatis Plus 数据库操作
- **缓存**: Redis 高性能缓存
- **消息队列**: RabbitMQ 异步消息处理
- **API文档**: OpenAPI 3.0 with Swagger UI
- **安全**: Spring Security 认证和授权

## 🚀 快速开始

### 环境要求
- **前端**: Node.js >= 18.0.0, npm >= 8.0.0
- **后端**: Java 21+, Maven 3.6+
- **数据库**: H2（默认）、MySQL、PostgreSQL、Oracle或SQL Server
- **缓存**: Redis（可选）
- **消息队列**: RabbitMQ（可选）

### 开发环境搭建

#### 1. 克隆仓库
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP/TaskManager
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
前端将在 http://localhost:3003 启动

### 使用Docker

```bash
cd FastLCDP/docker
docker-compose up -d
```

## 📖 使用指南

### 开始使用任务管理

1. **创建项目**
   - 点击"新建项目"按钮
   - 设置项目名称、描述和成员
   - 配置项目权限和设置

2. **管理任务**
   - 在项目中创建任务
   - 设置任务优先级、截止日期和负责人
   - 添加任务描述和附件

3. **使用看板视图**
   - 切换到看板视图
   - 拖拽任务在不同状态间移动
   - 自定义看板列和工作流

4. **团队协作**
   - 邀请团队成员加入项目
   - 在任务中添加评论和讨论
   - 使用@提及功能通知相关人员

5. **跟踪进度**
   - 查看项目仪表板
   - 分析任务完成情况
   - 生成进度报告

## 🔧 配置说明

### 后端配置
编辑后端目录中的 `application.yaml`：

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

### 前端配置
编辑 `vite.config.ts` 进行构建和开发设置：

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

## 🗺️ 发展路线图

- [ ] 甘特图视图
- [ ] 时间跟踪功能
- [ ] 自动化规则引擎
- [ ] 移动端原生应用
- [ ] 第三方集成（Slack、钉钉等）
- [ ] AI智能任务推荐
- [ ] 高级报表和分析
- [ ] 多语言国际化

---

**TaskManager** 是 [FastLCDP](../README.md) 平台的一部分 - 一个综合性的低代码开发生态系统。