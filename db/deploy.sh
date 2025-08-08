#!/bin/bash

# =====================================================
# 房屋租赁管理系统 - 自动部署脚本
# 版本: 1.0
# 创建时间: 2025-08-06
# 描述: 自动化部署数据库和应用程序
# =====================================================

set -e  # 遇到错误立即退出

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

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

# 检查命令是否存在
check_command() {
    if ! command -v $1 &> /dev/null; then
        log_error "$1 命令未找到，请先安装 $1"
        exit 1
    fi
}

# 检查MySQL连接
check_mysql_connection() {
    log_info "检查MySQL连接..."
    if mysql -u$MYSQL_USER -p$MYSQL_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT -e "SELECT 1;" &> /dev/null; then
        log_success "MySQL连接成功"
        return 0
    else
        log_error "MySQL连接失败，请检查连接参数"
        return 1
    fi
}

# 创建数据库用户
create_database_user() {
    log_info "创建数据库用户..."
    mysql -u$MYSQL_ROOT_USER -p$MYSQL_ROOT_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT << EOF
CREATE USER IF NOT EXISTS '$MYSQL_APP_USER'@'%' IDENTIFIED BY '$MYSQL_APP_PASSWORD';
GRANT ALL PRIVILEGES ON rent_house_management.* TO '$MYSQL_APP_USER'@'%';
FLUSH PRIVILEGES;
EOF
    log_success "数据库用户创建完成"
}

# 部署数据库
deploy_database() {
    log_info "开始部署数据库..."
    
    # 执行SQL脚本
    local scripts=(
        "01_create_database.sql"
        "02_create_tables.sql"
        "03_insert_initial_data.sql"
        "04_create_indexes.sql"
        "05_create_views.sql"
        "06_create_procedures.sql"
        "07_create_triggers.sql"
    )
    
    for script in "${scripts[@]}"; do
        if [ -f "$script" ]; then
            log_info "执行脚本: $script"
            mysql -u$MYSQL_ROOT_USER -p$MYSQL_ROOT_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT < "$script"
            log_success "脚本 $script 执行完成"
        else
            log_warning "脚本 $script 不存在，跳过"
        fi
    done
    
    log_success "数据库部署完成"
}

# 备份数据库
backup_database() {
    log_info "备份现有数据库..."
    local backup_file="backup_$(date +%Y%m%d_%H%M%S).sql"
    
    if mysqldump -u$MYSQL_ROOT_USER -p$MYSQL_ROOT_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT \
        --single-transaction --routines --triggers rent_house_management > "$backup_file" 2>/dev/null; then
        log_success "数据库备份完成: $backup_file"
    else
        log_warning "数据库备份失败或数据库不存在"
    fi
}

# 验证部署
verify_deployment() {
    log_info "验证部署结果..."
    
    # 检查表是否存在
    local tables=$(mysql -u$MYSQL_APP_USER -p$MYSQL_APP_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT \
        -D rent_house_management -e "SHOW TABLES;" 2>/dev/null | wc -l)
    
    if [ $tables -gt 5 ]; then
        log_success "数据库表创建成功 ($((tables-1)) 个表)"
    else
        log_error "数据库表创建失败"
        exit 1
    fi
    
    # 检查初始用户
    local users=$(mysql -u$MYSQL_APP_USER -p$MYSQL_APP_PASSWORD -h$MYSQL_HOST -P$MYSQL_PORT \
        -D rent_house_management -e "SELECT COUNT(*) FROM users;" 2>/dev/null | tail -n 1)
    
    if [ $users -gt 0 ]; then
        log_success "初始用户数据创建成功 ($users 个用户)"
    else
        log_error "初始用户数据创建失败"
        exit 1
    fi
}

# 显示部署信息
show_deployment_info() {
    log_success "部署完成！"
    echo
    echo "==================================="
    echo "  房屋租赁管理系统部署信息"
    echo "==================================="
    echo "数据库地址: $MYSQL_HOST:$MYSQL_PORT"
    echo "数据库名称: rent_house_management"
    echo "应用用户: $MYSQL_APP_USER"
    echo
    echo "初始管理员账号:"
    echo "  用户名: superadmin"
    echo "  密码: 123456"
    echo
    echo "⚠️  请及时修改默认密码！"
    echo "==================================="
}

# 主函数
main() {
    log_info "开始部署房屋租赁管理系统..."
    
    # 检查必要的命令
    check_command mysql
    check_command mysqldump
    
    # 设置默认值
    MYSQL_HOST=${MYSQL_HOST:-localhost}
    MYSQL_PORT=${MYSQL_PORT:-3306}
    MYSQL_ROOT_USER=${MYSQL_ROOT_USER:-root}
    MYSQL_APP_USER=${MYSQL_APP_USER:-rent_app}
    
    # 检查必要的环境变量
    if [ -z "$MYSQL_ROOT_PASSWORD" ]; then
        read -s -p "请输入MySQL root密码: " MYSQL_ROOT_PASSWORD
        echo
    fi
    
    if [ -z "$MYSQL_APP_PASSWORD" ]; then
        read -s -p "请输入应用数据库用户密码: " MYSQL_APP_PASSWORD
        echo
    fi
    
    # 检查MySQL连接
    MYSQL_USER=$MYSQL_ROOT_USER
    MYSQL_PASSWORD=$MYSQL_ROOT_PASSWORD
    if ! check_mysql_connection; then
        exit 1
    fi
    
    # 备份现有数据库
    backup_database
    
    # 创建应用用户
    create_database_user
    
    # 部署数据库
    deploy_database
    
    # 验证部署
    MYSQL_USER=$MYSQL_APP_USER
    MYSQL_PASSWORD=$MYSQL_APP_PASSWORD
    verify_deployment
    
    # 显示部署信息
    show_deployment_info
}

# 脚本入口
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi
