# FastLCDP Docker 部署指南

## 简化部署模式

本配置提供了一个简化的Docker部署方案，只包含FastLCDP应用服务，使用H2文件数据库，无需外部数据库和Nginx。

## 快速开始

### 1. 构建和启动应用

```bash
# 构建并启动应用
docker-compose up -d

# 查看日志
docker-compose logs -f fastlcdp-app
```

### 2. 访问应用

- **应用主页**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html
- **H2数据库控制台**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:file:./data/fastlcdp`
  - 用户名: `sa`
  - 密码: (留空)
- **健康检查**: http://localhost:8080/actuator/health

### 3. 数据持久化

数据库文件存储在 `./data` 目录中，该目录会被挂载到容器内，确保数据持久化。

### 4. 日志查看

应用日志存储在 `./logs` 目录中，可以直接查看：

```bash
# 查看应用日志
tail -f logs/fastlcdp.log
```

### 5. 停止应用

```bash
# 停止应用
docker-compose down

# 停止应用并删除数据卷
docker-compose down -v
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