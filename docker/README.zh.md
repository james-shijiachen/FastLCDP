# ERDesigner Docker部署

**中文** | [English](./README.md)

## 项目概述

本配置提供了ERDesigner项目的完整Docker部署方案，包含前端（Vue 3）和后端（Spring Boot）服务，使用H2文件数据库，支持本地开发和生产环境部署。

## 快速开始

### 方式一：使用启动脚本（推荐）

**Linux/macOS:**
```bash
# 在 docker 目录下执行
cd docker

# 启动生产环境
./start-erdesigner.sh start

# 启动开发环境
./start-erdesigner.sh dev

# 查看服务状态
./start-erdesigner.sh status

# 查看日志
./start-erdesigner.sh logs

# 停止服务
./start-erdesigner.sh stop
```

**Windows:**
```cmd
REM 在 docker 目录下执行
cd docker

REM 启动生产环境
start-erdesigner.bat start

REM 启动开发环境
start-erdesigner.bat dev

REM 查看服务状态
start-erdesigner.bat status
```

### 方式二：直接使用 Docker Compose

```bash
# 在 docker 目录下执行
cd docker

# 启动生产环境
docker-compose up -d

# 启动开发环境
docker-compose -f docker-compose.dev.yml up -d

# 查看服务状态
docker-compose ps
```

### 1. 构建和启动应用

```bash
# 构建并启动应用
docker-compose up -d

# 查看日志
docker-compose logs -f fastlcdp-app
```

### 2. 访问应用

- **ERDesigner前端**: http://localhost:3001
- **ERDesigner后端API**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **H2数据库控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/erdesigner`
  - 用户名: `sa`
  - 密码: (留空)
- **后端健康检查**: http://localhost:8080/actuator/health

### 3. 数据持久化

数据库文件存储在 `./data` 目录中，该目录会被挂载到容器内，确保数据持久化。

### 4. 日志查看

应用日志存储在 `./logs` 目录中，可以直接查看：

```bash
# 查看后端日志
docker-compose logs -f erdesigner-backend

# 查看前端日志
docker-compose logs -f erdesigner-frontend

# 查看所有服务日志
docker-compose logs -f
```

### 5. 停止应用

```bash
# 停止所有服务
docker-compose down

# 停止服务并删除数据卷
docker-compose down -v

# 重新构建并启动
docker-compose up --build -d
```

## 🏗️ 服务架构

### 服务组件
- **erdesigner-frontend**: Vue 3前端应用 (端口: 3001)
- **erdesigner-backend**: Spring Boot后端API (端口: 8080)
- **erdesigner-network**: 内部网络，连接前后端服务

### 数据持久化
- **数据库文件**: `./data/` 目录（H2数据库文件）
- **应用日志**: `./logs/` 目录

## 🔧 配置说明

### 环境变量

#### 后端环境变量
- `SPRING_PROFILES_ACTIVE=docker` - 激活Docker配置文件

#### 前端环境变量
- `VITE_API_BASE_URL=http://localhost:8080` - 后端API地址

### 自定义配置

如需修改配置，可以编辑 `docker-compose.yml` 文件：

```yaml
services:
  erdesigner-backend:
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_TYPE=MYSQL  # 切换到MySQL
      - DB_HOST=mysql
      - DB_PORT=3306
  
  erdesigner-frontend:
    environment:
      - VITE_API_BASE_URL=http://your-backend-url
```

## 🚀 生产环境部署

### 使用外部数据库

1. **添加MySQL服务**
```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: erdesigner
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

2. **更新后端配置**
```yaml
erdesigner-backend:
  environment:
    - SPRING_PROFILES_ACTIVE=prod
    - DB_TYPE=MYSQL
    - DB_HOST=mysql
    - DB_USERNAME=root
    - DB_PASSWORD=rootpassword
```

### 使用Nginx反向代理

```yaml
nginx:
  image: nginx:alpine
  ports:
    - "80:80"
    - "443:443"
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
  depends_on:
    - erdesigner-frontend
    - erdesigner-backend
```

## 🛠️ 开发模式

### 本地开发

如果只需要启动后端服务进行前端开发：

```bash
# 只启动后端
docker-compose up erdesigner-backend -d

# 前端本地开发
cd ../ERDesigner/frontend
npm install
npm run dev
```

### 热重载开发

修改 `docker-compose.yml` 添加卷挂载：

```yaml
erdesigner-frontend:
  volumes:
    - ../ERDesigner/frontend/src:/app/src
  command: npm run dev
```

## 📊 监控和调试

### 容器状态检查
```bash
# 查看容器状态
docker-compose ps

# 查看容器资源使用
docker stats

# 进入容器调试
docker-compose exec erdesigner-backend bash
docker-compose exec erdesigner-frontend sh
```

### 网络调试
```bash
# 查看网络
docker network ls
docker network inspect docker_erdesigner-network

# 测试服务连通性
docker-compose exec erdesigner-frontend wget -qO- http://erdesigner-backend:8080/actuator/health
```

## 配置说明

### 环境变量

- `SPRING_PROFILES_ACTIVE=docker`: 使用Docker配置文件

### 数据卷

- `./logs:/app/logs`: 日志文件持久化
- `./examples:/app/examples:ro`: 示例文件（只读）
- `./data:/app/data`: 数据库文件持久化

### 端口映射

- `8080:8080`: 应用服务端口

## 扩展配置

如果需要连接外部数据库，可以修改 `application-docker.yaml` 中的数据源配置，并在 `docker-compose.yml` 中添加相应的环境变量。

### 连接外部MySQL数据库示例

在 `docker-compose.yml` 中添加环境变量：

```yaml
environment:
  - SPRING_PROFILES_ACTIVE=docker
  - SPRING_DATASOURCE_URL=jdbc:mysql://your-mysql-host:3306/fastlcdp
  - SPRING_DATASOURCE_USERNAME=your-username
  - SPRING_DATASOURCE_PASSWORD=your-password
```

## 故障排除

### 应用启动失败

1. 检查日志：`docker-compose logs fastlcdp-app`
2. 确保端口8080未被占用
3. 检查数据目录权限

### 数据库连接问题

1. 确保 `./data` 目录存在且有写权限
2. 检查H2数据库文件是否正常创建

### 性能优化

1. 根据需要调整JVM内存参数
2. 修改Hikari连接池配置
3. 启用Spring Boot的懒加载功能