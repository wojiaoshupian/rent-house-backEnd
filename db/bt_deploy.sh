#!/bin/bash

# =====================================================
# 宝塔面板专用 - 自动部署脚本
# 版本: 1.0
# 创建时间: 2025-08-06
# 描述: 适用于宝塔面板环境的自动部署脚本
# =====================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 配置变量
PROJECT_NAME="rent-house-management"
WEB_ROOT="/www/wwwroot/${PROJECT_NAME}"
DB_NAME="rent_house"
DB_USER="rent_house"

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

# 检查宝塔面板是否安装
check_bt_panel() {
    if [ ! -f "/www/server/panel/BT-Panel" ]; then
        log_error "未检测到宝塔面板，请先安装宝塔面板"
        exit 1
    fi
    log_success "检测到宝塔面板"
}

# 检查必要软件
check_software() {
    log_info "检查必要软件..."
    
    # 检查MySQL
    if ! systemctl is-active --quiet mysqld && ! systemctl is-active --quiet mysql; then
        log_error "MySQL服务未运行，请在宝塔面板中启动MySQL"
        exit 1
    fi
    log_success "MySQL服务正常"
    
    # 检查Nginx
    if ! systemctl is-active --quiet nginx; then
        log_error "Nginx服务未运行，请在宝塔面板中启动Nginx"
        exit 1
    fi
    log_success "Nginx服务正常"
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        log_error "Java未安装，请在宝塔面板软件商店中安装Java"
        exit 1
    fi
    log_success "Java环境正常"
}

# 创建项目目录
create_directories() {
    log_info "创建项目目录..."
    
    mkdir -p "${WEB_ROOT}"
    mkdir -p "${WEB_ROOT}/logs"
    mkdir -p "${WEB_ROOT}/uploads"
    mkdir -p "${WEB_ROOT}/static"
    mkdir -p "${WEB_ROOT}/config"
    
    # 设置权限
    chown -R www:www "${WEB_ROOT}"
    chmod -R 755 "${WEB_ROOT}"
    
    log_success "项目目录创建完成"
}

# 导入数据库
import_database() {
    log_info "导入数据库..."
    
    if [ ! -f "bt_import_database.sql" ]; then
        log_error "数据库导入文件 bt_import_database.sql 不存在"
        exit 1
    fi
    
    # 获取MySQL root密码
    if [ -z "$MYSQL_ROOT_PASSWORD" ]; then
        read -s -p "请输入MySQL root密码: " MYSQL_ROOT_PASSWORD
        echo
    fi
    
    # 导入数据库
    mysql -u root -p"$MYSQL_ROOT_PASSWORD" < bt_import_database.sql
    
    log_success "数据库导入完成"
}

# 配置应用
configure_app() {
    log_info "配置应用..."
    
    # 获取数据库密码
    if [ -z "$DB_PASSWORD" ]; then
        read -s -p "请输入数据库用户 ${DB_USER} 的密码: " DB_PASSWORD
        echo
    fi
    
    # 复制配置文件
    if [ -f "application-bt.properties" ]; then
        cp application-bt.properties "${WEB_ROOT}/application-production.properties"
        
        # 替换配置中的密码
        sed -i "s/YOUR_DATABASE_PASSWORD/${DB_PASSWORD}/g" "${WEB_ROOT}/application-production.properties"
        
        log_success "应用配置完成"
    else
        log_warning "配置文件 application-bt.properties 不存在，请手动配置"
    fi
}

# 部署JAR包
deploy_jar() {
    log_info "部署JAR包..."
    
    # 查找JAR文件
    JAR_FILE=$(find . -name "*.jar" -type f | head -1)
    
    if [ -z "$JAR_FILE" ]; then
        log_error "未找到JAR文件，请确保已编译项目"
        exit 1
    fi
    
    # 复制JAR文件
    cp "$JAR_FILE" "${WEB_ROOT}/app.jar"
    chown www:www "${WEB_ROOT}/app.jar"
    
    log_success "JAR包部署完成: $JAR_FILE"
}

# 创建启动脚本
create_startup_script() {
    log_info "创建启动脚本..."
    
    cat > "${WEB_ROOT}/start.sh" << 'EOF'
#!/bin/bash
cd /www/wwwroot/rent-house-management
nohup java -Xms512m -Xmx1024m -Dspring.profiles.active=production -jar app.jar > logs/app.log 2>&1 &
echo $! > app.pid
echo "应用启动完成，PID: $(cat app.pid)"
EOF

    cat > "${WEB_ROOT}/stop.sh" << 'EOF'
#!/bin/bash
cd /www/wwwroot/rent-house-management
if [ -f app.pid ]; then
    PID=$(cat app.pid)
    kill $PID
    rm -f app.pid
    echo "应用已停止"
else
    echo "应用未运行"
fi
EOF

    cat > "${WEB_ROOT}/restart.sh" << 'EOF'
#!/bin/bash
cd /www/wwwroot/rent-house-management
./stop.sh
sleep 3
./start.sh
EOF

    chmod +x "${WEB_ROOT}"/*.sh
    chown www:www "${WEB_ROOT}"/*.sh
    
    log_success "启动脚本创建完成"
}

# 配置Nginx
configure_nginx() {
    log_info "配置Nginx..."
    
    if [ -f "bt_nginx.conf" ]; then
        log_info "Nginx配置文件已准备好，请手动在宝塔面板中配置："
        log_info "1. 进入宝塔面板 -> 网站 -> 设置 -> 配置文件"
        log_info "2. 替换配置文件内容为 bt_nginx.conf 中的内容"
        log_info "3. 修改域名/IP地址"
        log_info "4. 保存并重载Nginx"
    else
        log_warning "Nginx配置模板不存在"
    fi
}

# 启动应用
start_application() {
    log_info "启动应用..."
    
    cd "${WEB_ROOT}"
    ./start.sh
    
    # 等待应用启动
    sleep 10
    
    # 检查应用是否启动成功
    if curl -f http://localhost:8080/actuator/health &> /dev/null; then
        log_success "应用启动成功"
    else
        log_error "应用启动失败，请检查日志: ${WEB_ROOT}/logs/app.log"
        exit 1
    fi
}

# 显示部署信息
show_deployment_info() {
    log_success "部署完成！"
    echo
    echo "==================================="
    echo "  宝塔面板部署信息"
    echo "==================================="
    echo "项目路径: ${WEB_ROOT}"
    echo "数据库名: ${DB_NAME}"
    echo "数据库用户: ${DB_USER}"
    echo "应用端口: 8080"
    echo
    echo "管理脚本:"
    echo "  启动: ${WEB_ROOT}/start.sh"
    echo "  停止: ${WEB_ROOT}/stop.sh"
    echo "  重启: ${WEB_ROOT}/restart.sh"
    echo
    echo "日志文件:"
    echo "  应用日志: ${WEB_ROOT}/logs/app.log"
    echo "  Nginx日志: ${WEB_ROOT}/logs/nginx_*.log"
    echo
    echo "初始管理员账号:"
    echo "  用户名: superadmin"
    echo "  密码: 123456"
    echo
    echo "⚠️  请及时修改默认密码！"
    echo "⚠️  请在宝塔面板中配置Nginx反向代理！"
    echo "==================================="
}

# 主函数
main() {
    log_info "开始宝塔面板部署..."
    
    check_bt_panel
    check_software
    create_directories
    import_database
    configure_app
    deploy_jar
    create_startup_script
    configure_nginx
    start_application
    show_deployment_info
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
