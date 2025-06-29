#!/bin/bash

# ERDesigner 启动脚本
# 用于快速启动 ERDesigner 前后端服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    echo -e "${2}${1}${NC}"
}

# 检查Docker是否运行
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_message "错误: Docker 未运行，请先启动 Docker" "$RED"
        exit 1
    fi
}

# 检查Docker Compose是否可用
check_docker_compose() {
    if ! command -v docker-compose > /dev/null 2>&1; then
        print_message "错误: docker-compose 未安装" "$RED"
        exit 1
    fi
}

# 显示帮助信息
show_help() {
    echo "ERDesigner 启动脚本"
    echo ""
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  start     启动 ERDesigner 服务 (生产模式)"
    echo "  dev       启动 ERDesigner 服务 (开发模式)"
    echo "  stop      停止 ERDesigner 服务"
    echo "  restart   重启 ERDesigner 服务"
    echo "  logs      查看服务日志"
    echo "  status    查看服务状态"
    echo "  clean     清理所有容器和数据卷"
    echo "  help      显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0 start    # 启动生产环境"
    echo "  $0 dev      # 启动开发环境"
    echo "  $0 logs     # 查看日志"
}

# 启动服务
start_services() {
    local mode=$1
    local compose_file="docker-compose.yml"
    
    if [ "$mode" = "dev" ]; then
        compose_file="docker-compose.dev.yml"
        print_message "启动 ERDesigner 开发环境..." "$BLUE"
    else
        print_message "启动 ERDesigner 生产环境..." "$BLUE"
    fi
    
    docker-compose -f "$compose_file" up -d
    
    if [ $? -eq 0 ]; then
        print_message "ERDesigner 服务启动成功!" "$GREEN"
        echo ""
        print_message "访问地址:" "$YELLOW"
        echo "  前端应用: http://localhost:3001"
        echo "  后端API:  http://localhost:8080"
        echo "  H2控制台: http://localhost:8080/h2-console"
        echo "  API文档:  http://localhost:8080/swagger-ui.html"
        echo ""
        print_message "使用 '$0 logs' 查看日志" "$YELLOW"
        print_message "使用 '$0 stop' 停止服务" "$YELLOW"
    else
        print_message "ERDesigner 服务启动失败!" "$RED"
        exit 1
    fi
}

# 停止服务
stop_services() {
    print_message "停止 ERDesigner 服务..." "$BLUE"
    
    # 尝试停止两个compose文件
    docker-compose -f docker-compose.yml down 2>/dev/null || true
    docker-compose -f docker-compose.dev.yml down 2>/dev/null || true
    
    print_message "ERDesigner 服务已停止" "$GREEN"
}

# 重启服务
restart_services() {
    local mode=$1
    print_message "重启 ERDesigner 服务..." "$BLUE"
    stop_services
    sleep 2
    start_services "$mode"
}

# 查看日志
show_logs() {
    local service=$1
    
    if [ -z "$service" ]; then
        print_message "显示所有服务日志 (按 Ctrl+C 退出):" "$BLUE"
        # 检查哪个compose文件在运行
        if docker-compose -f docker-compose.yml ps -q > /dev/null 2>&1; then
            docker-compose -f docker-compose.yml logs -f
        elif docker-compose -f docker-compose.dev.yml ps -q > /dev/null 2>&1; then
            docker-compose -f docker-compose.dev.yml logs -f
        else
            print_message "没有运行的服务" "$YELLOW"
        fi
    else
        print_message "显示 $service 服务日志 (按 Ctrl+C 退出):" "$BLUE"
        if docker-compose -f docker-compose.yml ps -q > /dev/null 2>&1; then
            docker-compose -f docker-compose.yml logs -f "$service"
        elif docker-compose -f docker-compose.dev.yml ps -q > /dev/null 2>&1; then
            docker-compose -f docker-compose.dev.yml logs -f "$service"
        else
            print_message "没有运行的服务" "$YELLOW"
        fi
    fi
}

# 查看状态
show_status() {
    print_message "ERDesigner 服务状态:" "$BLUE"
    echo ""
    
    # 检查生产环境
    if docker-compose -f docker-compose.yml ps -q > /dev/null 2>&1; then
        print_message "生产环境:" "$YELLOW"
        docker-compose -f docker-compose.yml ps
    fi
    
    # 检查开发环境
    if docker-compose -f docker-compose.dev.yml ps -q > /dev/null 2>&1; then
        print_message "开发环境:" "$YELLOW"
        docker-compose -f docker-compose.dev.yml ps
    fi
    
    # 如果都没有运行
    if ! docker-compose -f docker-compose.yml ps -q > /dev/null 2>&1 && \
       ! docker-compose -f docker-compose.dev.yml ps -q > /dev/null 2>&1; then
        print_message "没有运行的 ERDesigner 服务" "$YELLOW"
    fi
}

# 清理环境
clean_environment() {
    print_message "清理 ERDesigner 环境..." "$BLUE"
    
    # 停止并删除容器
    docker-compose -f docker-compose.yml down -v 2>/dev/null || true
    docker-compose -f docker-compose.dev.yml down -v 2>/dev/null || true
    
    # 删除相关镜像
    docker images | grep erdesigner | awk '{print $3}' | xargs -r docker rmi -f 2>/dev/null || true
    
    # 清理未使用的资源
    docker system prune -f
    
    print_message "ERDesigner 环境清理完成" "$GREEN"
}

# 主函数
main() {
    # 切换到脚本所在目录
    cd "$(dirname "$0")"
    
    # 检查依赖
    check_docker
    check_docker_compose
    
    case "$1" in
        start)
            start_services "prod"
            ;;
        dev)
            start_services "dev"
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services "prod"
            ;;
        restart-dev)
            restart_services "dev"
            ;;
        logs)
            show_logs "$2"
            ;;
        status)
            show_status
            ;;
        clean)
            read -p "确定要清理所有 ERDesigner 数据吗? (y/N): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                clean_environment
            else
                print_message "取消清理操作" "$YELLOW"
            fi
            ;;
        help|--help|-h)
            show_help
            ;;
        "")
            show_help
            ;;
        *)
            print_message "未知选项: $1" "$RED"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# 运行主函数
main "$@"