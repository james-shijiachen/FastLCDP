# FastLCDP 多项目 ContextPath 配置指南

## 概述

本配置方案通过 **统一端口 + ContextPath** 的方式来区分 FastLCDP 平台的三个核心项目：ERDesigner、ProcessEngineer 和 TaskManager。

## 访问地址规划

### 前端访问地址（端口 3000）
- **ERDesigner**: http://localhost:3000/erdesigner
- **ProcessEngineer**: http://localhost:3000/processengineer
- **TaskManager**: http://localhost:3000/taskmanager

### 后端 API 地址（端口 8080）
- **ERDesigner**: http://localhost:8080/erdesigner
- **ProcessEngineer**: http://localhost:8080/processengineer
- **TaskManager**: http://localhost:8080/taskmanager

## 架构设计

```
┌─────────────────────────────────────────────────────────────┐
│                    Nginx 反向代理                            │
│  前端入口: :3000          后端入口: :8080                    │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              │               │               │
    ┌─────────▼─────────┐ ┌───▼────┐ ┌────────▼────────┐
    │   ERDesigner     │ │Process │ │   TaskManager   │
    │ Frontend: :3001  │ │Engineer│ │ Frontend: :3003 │
    │ Backend:  :8081  │ │F: :3002│ │ Backend:  :8083 │
    └──────────────────┘ │B: :8082│ └─────────────────┘
                         └────────┘
```

## 配置文件说明

### 1. Docker Compose 配置

**文件**: `docker-compose.multi.yml`

- 使用 Nginx 作为反向代理
- 每个项目的前后端使用独立的内部端口
- 通过环境变量设置 ContextPath

### 2. Nginx 配置

**文件**: `nginx.conf`

- 前端服务器监听 3000 端口
- 后端服务器监听 8080 端口
- 根据 URL 路径路由到对应的内部服务

### 3. 后端配置

每个项目的 `application.yaml` 配置：

```yaml
server:
  port: 808X  # ERDesigner: 8081, ProcessEngineer: 8082, TaskManager: 8083
  servlet:
    context-path: /项目名称
```

### 4. 前端配置

每个项目的 `vite.config.ts` 配置：

```typescript
export default defineConfig({
  base: process.env.VITE_BASE_PATH || '/项目名称/',
  server: {
    port: 300X  // ERDesigner: 3001, ProcessEngineer: 3002, TaskManager: 3003
  }
})
```

## 启动方式

### 方式一：使用启动脚本（推荐）

**Linux/macOS**:
```bash
cd docker
./start-multi-projects.sh
```

**Windows**:
```cmd
cd docker
start-multi-projects.bat
```

### 方式二：手动启动

```bash
cd docker
docker-compose -f docker-compose.multi.yml up --build -d
```

## 端口分配表

| 项目 | 前端内部端口 | 后端内部端口 | 前端访问地址 | 后端访问地址 |
|------|-------------|-------------|-------------|-------------|
| ERDesigner | 3001 | 8081 | http://localhost:3000/erdesigner | http://localhost:8080/erdesigner |
| ProcessEngineer | 3002 | 8082 | http://localhost:3000/processengineer | http://localhost:8080/processengineer |
| TaskManager | 3003 | 8083 | http://localhost:3000/taskmanager | http://localhost:8080/taskmanager |

## 开发环境配置

### 本地开发模式

如果需要在本地开发环境中使用相同的 ContextPath 配置：

1. **后端开发**：
   - 修改各项目的 `application.yaml`
   - 设置对应的 `server.servlet.context-path`

2. **前端开发**：
   - 设置环境变量 `VITE_BASE_PATH`
   - 或直接修改 `vite.config.ts` 中的 `base` 配置

### 环境变量

```bash
# ERDesigner
export VITE_BASE_PATH=/erdesigner/
export VITE_API_BASE_URL=http://localhost:8080/erdesigner

# ProcessEngineer
export VITE_BASE_PATH=/processengineer/
export VITE_API_BASE_URL=http://localhost:8080/processengineer

# TaskManager
export VITE_BASE_PATH=/taskmanager/
export VITE_API_BASE_URL=http://localhost:8080/taskmanager
```

## 常用命令

### 查看服务状态
```bash
docker-compose -f docker-compose.multi.yml ps
```

### 查看日志
```bash
# 查看所有服务日志
docker-compose -f docker-compose.multi.yml logs -f

# 查看特定服务日志
docker-compose -f docker-compose.multi.yml logs -f erdesigner-frontend
```

### 停止服务
```bash
docker-compose -f docker-compose.multi.yml down
```

### 重新构建
```bash
docker-compose -f docker-compose.multi.yml up --build -d
```

## 故障排除

### 1. 端口冲突
如果遇到端口冲突，检查以下端口是否被占用：
- 3000 (Nginx 前端入口)
- 8080 (Nginx 后端入口)
- 3001, 3002, 3003 (各项目前端内部端口)
- 8081, 8082, 8083 (各项目后端内部端口)

### 2. 路由问题
- 检查 Nginx 配置文件 `nginx.conf`
- 确认各项目的 ContextPath 配置正确

### 3. 服务启动失败
- 检查 Docker 是否正常运行
- 查看具体服务的日志信息
- 确认项目配置文件是否正确

## 优势

1. **统一入口**：前后端分别使用统一的端口访问
2. **清晰分离**：通过 ContextPath 明确区分不同项目
3. **易于扩展**：新增项目只需添加对应的路由配置
4. **生产就绪**：配置方案适用于生产环境部署
5. **开发友好**：支持本地开发环境的相同配置

## 注意事项

1. 确保所有项目的静态资源路径配置正确
2. API 调用时需要包含正确的 ContextPath
3. 前端路由配置需要考虑 base path
4. 数据库连接配置需要为每个项目使用独立的数据库