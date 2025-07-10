# FastLCDP - 快速低代码开发平台

**中文** | [English](README.md)

[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)
[![GitHub Issues](https://img.shields.io/github/issues/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/issues)
[![GitHub Stars](https://img.shields.io/github/stars/james-shijiachen/fastLCDP)](https://github.com/james-shijiachen/fastLCDP/stargazers)

---

## 🚀 项目简介

**FastLCDP** 是一个基于 AI 的低代码开发平台，让 AI 像人一样参与软件开发过程，通过人机协作加速项目交付。平台包含三大核心模块：

- [**ERDesigner**](./ERDesigner/README.zh.md)：现代化 ER 图设计工具，支持可视化建模与 AI 智能编辑。
- [**ProcessEngineer**](./ProcessEngineer/README.zh.md)：可视化业务流程设计器，支持 BPMN 标准与主流 BPM 引擎。
- [**TaskManager**](./TaskManager/README.zh.md)：智能任务管理系统，支持看板、团队协作与实时同步。

---

## 🛠️ 技术栈
- **后端**：Java 21、Spring Boot 3.5+、MyBatis Plus、Undertow、Log4j2、Spring Security、OpenAPI、JAXB、Lombok
- **前端**：Vue 3、TypeScript、Vite、Pinia、vue-i18n、Fabric.js
- **数据库**：MySQL、PostgreSQL、Oracle、SQL Server、H2
- **容器化**：Docker、Docker Compose
- **AI平台**: Claude-3.7 Sonnet, Gemini 2.5 Pro, GPT-4, DeepSeek V3, Qwen 2.5-Max

---

## 📁 项目结构
```
FastLCDP/
├── ERDesigner/              # ER 图设计工具
│   ├── backend/             # Spring Boot 后端
│   └── frontend/            # Vue 3 前端
├── ProcessEngineer/         # 业务流程设计器
│   ├── backend/             # Spring Boot 后端
│   └── frontend/            # Vue 3 前端
├── TaskManager/             # 任务管理系统
│   ├── backend/             # Spring Boot 后端
│   └── frontend/            # Vue 3 前端
├── docker/                  # Docker 配置
├── docs/                    # 文档
└── rules/                   # 代码规范与 Cursor AI 规则
```

---

## 📦 开发规范
- 统一代码风格、命名、注释、异常处理
- RESTful API 设计、国际化与安全最佳实践
- [详细规则与最佳实践](rules/cursor/personal-rules.md)

---

## ⚡ 快速开始

### 推荐：Docker 一键启动
```bash
git clone https://github.com/james-shijiachen/FastLCDP.git
cd FastLCDP
cd docker
docker-compose up -d
```
- ERDesigner 前端：http://localhost:3001/ERDesigner
- ERDesigner 后端 API：http://localhost:8080/ERDesigner/api
- API 文档：http://localhost:8080/ERDesigner/swagger-ui.html

### 手动安装
#### ERDesigner 后端
```bash
cd ERDesigner/backend
mvn clean install
mvn spring-boot:run
```
#### ERDesigner 前端
```bash
cd ERDesigner/frontend
npm install
npm run dev
```

---

## 🧑‍💻 常用命令
- 后端：`mvn clean compile`、`mvn test`、`mvn spring-boot:run`、`mvn clean package`
- 前端：`npm install`、`npm run dev`、`npm run build`、`npm run preview`
- Docker：`docker build -t fastlcdp .`、`docker-compose up -d`、`docker-compose logs -f`

---

## 🤝 贡献指南
1. Fork 本项目
2. 创建特性分支 (`git checkout -b feature/your-feature`)
3. 提交更改 (`git commit -m 'feat: 新增功能说明'`)
4. 推送到分支 (`git push origin feature/your-feature`)
5. 创建 Pull Request

**贡献建议：**
- 遵循 [personal-rules.md](rules/cursor/personal-rules.md) 代码规范与提交信息格式
- 新功能需补充单元测试
- 提交前确保所有检查通过

---

## ❓ 常见问题
- **Q: 如何保证规范与项目同步？**
  - 定期审查并更新 [personal-rules.md](rules/cursor/personal-rules.md) 以反映最佳实践。
- **Q: FastLCDP 可用于商业项目吗？**
  - 可以，遵循 Apache 2.0 许可协议。
- **Q: 哪里可以查看详细开发规范？**
  - 见 [rules/cursor/personal-rules.md](rules/cursor/personal-rules.md)

---

## 📄 许可证
本项目采用 Apache License 2.0 许可证，详见 [LICENSE](https://github.com/james-shijiachen/fastLCDP/blob/main/LICENSE)。

## 📬 联系方式
- 项目主页：[https://github.com/james-shijiachen/fastLCDP](https://github.com/james-shijiachen/fastLCDP)
- 问题反馈：[https://github.com/james-shijiachen/fastLCDP/issues](https://github.com/james-shijiachen/fastLCDP/issues)
- 邮箱：[shijiachen@traninfo.com.cn](mailto:shijiachen@traninfo.com.cn)

---

**FastLCDP 团队** - 拥抱 AI，拥抱未来！🚀