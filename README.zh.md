# FastLCDP - 快速低代码数据库平台

**中文** | [English](README.md)

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

## 项目简介

FastLCDP是一款基于AI的低代码开发平台，产品的核心理念是让AI像人一样参与到软件项目的研发过程中，通过人和AI的完美结合加速软件项目的交付。平台包括以下几个核心产品：

### [**ERDesigner**](./ERDesigner/README.md)
- **现代化的ER图设计工具**：提供实体关系建模的可视化界面，支持实体、关系的创建、编辑和删除。
- **可视化编辑能力**：基于AI的图编辑模型，用户无需复杂的代码编写，即可完成实体关系的建模。

### [**ProcessEngineer**](./ProcessEngineer/README.md)
- **业务流程设计器**：提供可视化的业务流程建模工具，支持BPMN标准的流程设计和管理。
- **流程引擎集成**：支持Activiti、Flowable、Camunda等主流BPM引擎，实现流程的部署和执行。

### [**TaskManager**](./TaskManager/README.md)
- **智能任务管理系统**：提供项目管理和团队协作功能，支持任务分配、进度跟踪和团队协作。
- **可视化看板**：采用看板视图管理任务状态，支持拖拽式操作和实时同步。

## 🚀 快速开始

### 使用Docker（推荐）

1. **克隆项目**
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP
```

2. **使用Docker启动**
```bash
cd docker
docker-compose up -d
```

3. **访问应用**
- ERDesigner前端: http://localhost:3001/ERDesigner
- ERDesigner后端API: http://localhost:8080/ERDesigner/api
- API文档: http://localhost:8080/ERDesigner/swagger-ui.html

### 二、手动安装

1. ** ERDesigner后端(示例) **
```bash
cd ERDesigner/backend
mvn clean install
mvn spring-boot:run
```

2. ** ERDesigner前端(示例) **
```bash
cd ERDesigner/frontend
npm install
npm run dev
```

## 📁 项目结构

```
FastLCDP/
├── ERDesigner/              # ER图设计工具
│   ├── backend/             # Spring Boot后端
│   └── frontend/            # Vue 3前端
├── docker/                  # Docker配置
└── docs/                    # 文档
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 Apache License 2.0 许可证 - 查看 [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE) 文件了解详情。

## 联系方式

- 项目主页: [https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- 问题反馈: [https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- 邮箱: [shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP Team** - 拥抱AI，拥抱未来！🚀