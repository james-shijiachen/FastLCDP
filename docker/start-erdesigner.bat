@echo off
REM ERDesigner 启动脚本 (Windows版本)
REM 用于快速启动 ERDesigner 前后端服务

setlocal enabledelayedexpansion

REM 切换到脚本所在目录
cd /d "%~dp0"

REM 检查Docker是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo [错误] Docker 未运行，请先启动 Docker
    pause
    exit /b 1
)

REM 检查docker-compose是否可用
docker-compose --version >nul 2>&1
if errorlevel 1 (
    echo [错误] docker-compose 未安装
    pause
    exit /b 1
)

REM 显示帮助信息
if "%1"=="" goto :show_help
if "%1"=="help" goto :show_help
if "%1"=="--help" goto :show_help
if "%1"=="-h" goto :show_help

REM 处理命令
if "%1"=="start" goto :start_prod
if "%1"=="dev" goto :start_dev
if "%1"=="stop" goto :stop_services
if "%1"=="restart" goto :restart_prod
if "%1"=="restart-dev" goto :restart_dev
if "%1"=="logs" goto :show_logs
if "%1"=="status" goto :show_status
if "%1"=="clean" goto :clean_environment

echo [错误] 未知选项: %1
echo.
goto :show_help

:show_help
echo ERDesigner 启动脚本 (Windows版本)
echo.
echo 用法: %0 [选项]
echo.
echo 选项:
echo   start     启动 ERDesigner 服务 (生产模式)
echo   dev       启动 ERDesigner 服务 (开发模式)
echo   stop      停止 ERDesigner 服务
echo   restart   重启 ERDesigner 服务
echo   logs      查看服务日志
echo   status    查看服务状态
echo   clean     清理所有容器和数据卷
echo   help      显示此帮助信息
echo.
echo 示例:
echo   %0 start    # 启动生产环境
echo   %0 dev      # 启动开发环境
echo   %0 logs     # 查看日志
echo.
pause
exit /b 0

:start_prod
echo [信息] 启动 ERDesigner 生产环境...
docker-compose -f docker-compose.yml up -d
if errorlevel 1 (
    echo [错误] ERDesigner 服务启动失败!
    pause
    exit /b 1
)
goto :show_access_info

:start_dev
echo [信息] 启动 ERDesigner 开发环境...
docker-compose -f docker-compose.dev.yml up -d
if errorlevel 1 (
    echo [错误] ERDesigner 服务启动失败!
    pause
    exit /b 1
)
goto :show_access_info

:show_access_info
echo.
echo [成功] ERDesigner 服务启动成功!
echo.
echo 访问地址:
echo   前端应用: http://localhost:3001
echo   后端API:  http://localhost:8080
echo   H2控制台: http://localhost:8080/h2-console
echo   API文档:  http://localhost:8080/swagger-ui.html
echo.
echo 使用 '%0 logs' 查看日志
echo 使用 '%0 stop' 停止服务
echo.
pause
exit /b 0

:stop_services
echo [信息] 停止 ERDesigner 服务...
docker-compose -f docker-compose.yml down 2>nul
docker-compose -f docker-compose.dev.yml down 2>nul
echo [成功] ERDesigner 服务已停止
pause
exit /b 0

:restart_prod
echo [信息] 重启 ERDesigner 服务...
call :stop_services
timeout /t 2 /nobreak >nul
call :start_prod
exit /b 0

:restart_dev
echo [信息] 重启 ERDesigner 开发服务...
call :stop_services
timeout /t 2 /nobreak >nul
call :start_dev
exit /b 0

:show_logs
echo [信息] 显示服务日志 (按 Ctrl+C 退出):
echo.
REM 检查哪个compose文件在运行
docker-compose -f docker-compose.yml ps -q >nul 2>&1
if not errorlevel 1 (
    docker-compose -f docker-compose.yml logs -f
    goto :end
)
docker-compose -f docker-compose.dev.yml ps -q >nul 2>&1
if not errorlevel 1 (
    docker-compose -f docker-compose.dev.yml logs -f
    goto :end
)
echo [警告] 没有运行的服务
pause
exit /b 0

:show_status
echo [信息] ERDesigner 服务状态:
echo.
REM 检查生产环境
docker-compose -f docker-compose.yml ps -q >nul 2>&1
if not errorlevel 1 (
    echo 生产环境:
    docker-compose -f docker-compose.yml ps
    echo.
)
REM 检查开发环境
docker-compose -f docker-compose.dev.yml ps -q >nul 2>&1
if not errorlevel 1 (
    echo 开发环境:
    docker-compose -f docker-compose.dev.yml ps
    echo.
)
REM 如果都没有运行
docker-compose -f docker-compose.yml ps -q >nul 2>&1
if errorlevel 1 (
    docker-compose -f docker-compose.dev.yml ps -q >nul 2>&1
    if errorlevel 1 (
        echo [警告] 没有运行的 ERDesigner 服务
    )
)
pause
exit /b 0

:clean_environment
echo [警告] 确定要清理所有 ERDesigner 数据吗? (y/N): 
set /p confirm=
if /i "!confirm!"=="y" (
    echo [信息] 清理 ERDesigner 环境...
    docker-compose -f docker-compose.yml down -v 2>nul
    docker-compose -f docker-compose.dev.yml down -v 2>nul
    REM 删除相关镜像
    for /f "tokens=3" %%i in ('docker images ^| findstr erdesigner') do docker rmi -f %%i 2>nul
    REM 清理未使用的资源
    docker system prune -f
    echo [成功] ERDesigner 环境清理完成
) else (
    echo [信息] 取消清理操作
)
pause
exit /b 0

:end
exit /b 0