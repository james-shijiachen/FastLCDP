@echo off
chcp 65001 >nul

echo === FastLCDP 多项目启动脚本 ===
echo 前端统一访问地址: http://localhost:3000
echo 后端统一访问地址: http://localhost:8080
echo.
echo 项目访问地址:
echo - ERDesigner 前端: http://localhost:3000/erdesigner
echo - ERDesigner 后端: http://localhost:8080/erdesigner
echo - ProcessEngineer 前端: http://localhost:3000/processengineer
echo - ProcessEngineer 后端: http://localhost:8080/processengineer
echo - TaskManager 前端: http://localhost:3000/taskmanager
echo - TaskManager 后端: http://localhost:8080/taskmanager
echo.

REM 检查Docker是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker 未运行，请先启动 Docker
    pause
    exit /b 1
)

REM 停止现有容器
echo 🛑 停止现有容器...
docker-compose -f docker-compose.multi.yml down

REM 询问是否清理旧镜像
set /p cleanup="是否清理旧的Docker镜像? (y/N): "
if /i "%cleanup%"=="y" (
    echo 🧹 清理旧镜像...
    docker system prune -f
)

REM 构建并启动服务
echo 🚀 构建并启动所有服务...
docker-compose -f docker-compose.multi.yml up --build -d

REM 等待服务启动
echo ⏳ 等待服务启动...
timeout /t 10 /nobreak >nul

REM 检查服务状态
echo 📊 检查服务状态:
docker-compose -f docker-compose.multi.yml ps

echo.
echo ✅ 启动完成！
echo 📱 前端访问地址: http://localhost:3000
echo 🔧 后端API地址: http://localhost:8080
echo 📚 查看日志: docker-compose -f docker-compose.multi.yml logs -f
echo 🛑 停止服务: docker-compose -f docker-compose.multi.yml down
echo.
echo 🎯 项目入口:
echo    ERDesigner: http://localhost:3000/erdesigner
echo    ProcessEngineer: http://localhost:3000/processengineer
echo    TaskManager: http://localhost:3000/taskmanager
echo.
pause