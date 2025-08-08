#!/bin/bash

# =====================================================
# 房屋租赁管理系统 - 快速启动脚本
# 版本: 1.0.0
# 数据库密码: Qwesdx1245@
# =====================================================

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置变量
JAR_FILE="rent-house-management-1.0.0.jar"
APP_NAME="房屋租赁管理系统"
PID_FILE="app.pid"
LOG_FILE="app.log"

# 日志函数
log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 检查Java环境
check_java() {
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请先安装JDK 17+"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 11 ]; then
        log_error "Java版本过低，需要JDK 11+，当前版本: $JAVA_VERSION"
        exit 1
    fi
    
    log_success "Java环境检查通过，版本: $(java -version 2>&1 | head -n 1)"
}

# 检查JAR文件
check_jar() {
    if [ ! -f "$JAR_FILE" ]; then
        log_error "JAR文件不存在: $JAR_FILE"
        exit 1
    fi
    log_success "JAR文件检查通过: $JAR_FILE"
}

# 检查端口占用
check_port() {
    if lsof -Pi :8080 -sTCP:LISTEN -t >/dev/null ; then
        log_warning "端口8080已被占用，请先停止占用端口的进程"
        lsof -Pi :8080 -sTCP:LISTEN
        read -p "是否强制停止占用端口的进程? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            lsof -ti:8080 | xargs kill -9
            log_success "已停止占用端口的进程"
        else
            exit 1
        fi
    fi
}

# 启动应用
start_app() {
    log_info "启动 $APP_NAME..."
    
    # 设置JVM参数
    JVM_OPTS="-Xms512m -Xmx1024m"
    JVM_OPTS="$JVM_OPTS -Dspring.profiles.active=production"
    JVM_OPTS="$JVM_OPTS -Dfile.encoding=UTF-8"
    JVM_OPTS="$JVM_OPTS -Duser.timezone=Asia/Shanghai"
    
    # 启动应用
    nohup java $JVM_OPTS -jar "$JAR_FILE" > "$LOG_FILE" 2>&1 &
    
    # 保存PID
    echo $! > "$PID_FILE"
    
    log_success "应用启动中，PID: $(cat $PID_FILE)"
    log_info "日志文件: $LOG_FILE"
    
    # 等待应用启动
    log_info "等待应用启动..."
    sleep 5
    
    # 检查应用状态
    for i in {1..12}; do
        if curl -f http://localhost:8080/actuator/health &> /dev/null; then
            log_success "$APP_NAME 启动成功！"
            echo
            echo "==================================="
            echo "  $APP_NAME 已启动"
            echo "==================================="
            echo "访问地址: http://localhost:8080"
            echo "API文档: http://localhost:8080/swagger-ui/index.html"
            echo "健康检查: http://localhost:8080/actuator/health"
            echo
            echo "初始管理员账号:"
            echo "  用户名: superadmin"
            echo "  密码: 123456"
            echo
            echo "数据库配置:"
            echo "  地址: localhost:3306"
            echo "  数据库: rent_house"
            echo "  用户名: rent_house"
            echo "  密码: Qwesdx1245@"
            echo "==================================="
            return 0
        fi
        echo -n "."
        sleep 5
    done
    
    log_error "应用启动失败，请检查日志: $LOG_FILE"
    tail -n 20 "$LOG_FILE"
    exit 1
}

# 停止应用
stop_app() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null; then
            log_info "停止应用，PID: $PID"
            kill $PID
            sleep 3
            if ps -p $PID > /dev/null; then
                log_warning "强制停止应用"
                kill -9 $PID
            fi
            rm -f "$PID_FILE"
            log_success "应用已停止"
        else
            log_warning "应用进程不存在"
            rm -f "$PID_FILE"
        fi
    else
        log_warning "PID文件不存在，应用可能未运行"
    fi
}

# 重启应用
restart_app() {
    log_info "重启 $APP_NAME..."
    stop_app
    sleep 2
    start_app
}

# 查看状态
status_app() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p $PID > /dev/null; then
            log_success "$APP_NAME 正在运行，PID: $PID"
            if curl -f http://localhost:8080/actuator/health &> /dev/null; then
                log_success "应用健康检查通过"
            else
                log_warning "应用健康检查失败"
            fi
        else
            log_error "$APP_NAME 进程不存在"
            rm -f "$PID_FILE"
        fi
    else
        log_error "$APP_NAME 未运行"
    fi
}

# 查看日志
logs_app() {
    if [ -f "$LOG_FILE" ]; then
        tail -f "$LOG_FILE"
    else
        log_error "日志文件不存在: $LOG_FILE"
    fi
}

# 显示帮助
show_help() {
    echo "用法: $0 {start|stop|restart|status|logs|help}"
    echo
    echo "命令说明:"
    echo "  start   - 启动应用"
    echo "  stop    - 停止应用"
    echo "  restart - 重启应用"
    echo "  status  - 查看应用状态"
    echo "  logs    - 查看应用日志"
    echo "  help    - 显示帮助信息"
}

# 主函数
main() {
    case "$1" in
        start)
            check_java
            check_jar
            check_port
            start_app
            ;;
        stop)
            stop_app
            ;;
        restart)
            check_java
            check_jar
            restart_app
            ;;
        status)
            status_app
            ;;
        logs)
            logs_app
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            log_error "无效的命令: $1"
            show_help
            exit 1
            ;;
    esac
}

# 脚本入口
if [ $# -eq 0 ]; then
    log_info "启动 $APP_NAME..."
    main start
else
    main "$1"
fi
