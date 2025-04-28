#!/bin/bash

# 项目名 = 当前文件夹名
project_name=$(basename "$(pwd)")

# 输出日志函数（带颜色）
log_message() {
    local color_reset="\e[0m"
    local color_info="\e[32m"
    local color_warn="\e[33m"
    local color_error="\e[31m"

    case "$2" in
        info) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_info}$1${color_reset}" ;;
        warn) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_warn}$1${color_reset}" ;;
        error) echo -e "$(date '+%Y-%m-%d %H:%M:%S') ${color_error}$1${color_reset}" ;;
        *) echo "$(date '+%Y-%m-%d %H:%M:%S') $1" ;;
    esac
}

start_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    if [ ! -f "${jar_name}" ]; then
        log_message "Jar 文件不存在：${jar_name}" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -z "$PID" ]; then
        log_message "准备启动：${jar_name}" info
        nohup java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar "${jar_name}" > /dev/null 2>&1 &
        sleep 3
        PID=$(pgrep -f "${jar_name}")
        if [ -n "$PID" ]; then
            log_message "启动成功，PID: $PID" info
        else
            log_message "启动失败，请检查日志或确认是否已有其他实例运行。" error
        fi
    else
        log_message "程序已在运行中（PID: $PID），无需重复启动。" warn
    fi
}

stop_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        log_message "尝试停止进程：PID $PID" info
        kill "$PID"
        sleep 3
        PID2=$(pgrep -f "${jar_name}")
        if [ -z "$PID2" ]; then
            log_message "停止成功。" info
        else
            log_message "普通停止失败，尝试强制 kill -9..." warn
            kill -9 "$PID2"
            sleep 2
            PID3=$(pgrep -f "${jar_name}")
            if [ -z "$PID3" ]; then
                log_message "强制停止成功。" info
            else
                log_message "无法停止进程，请手动检查。" error
            fi
        fi
    else
        log_message "程序未运行，无需停止。" warn
    fi
}

restart_project() {
    version="$1"
    stop_project "$version"
    start_project "$version"
}

status_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        log_message "请指定版本号，例如：$0 {start|stop|restart|status} <version>" error
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        log_message "${jar_name} 正在运行，PID: $PID" info
    else
        log_message "${jar_name} 未运行。" warn
    fi
}

# 参数校验
if [ -z "$2" ]; then
    log_message "用法: $0 {start|stop|restart|status} <version>" error
    exit 1
fi

# 主执行逻辑
case "$1" in
    start)
        start_project "$2"
        ;;
    stop)
        stop_project "$2"
        ;;
    restart)
        restart_project "$2"
        ;;
    status)
        status_project "$2"
        ;;
    *)
        log_message "未知命令: $1" error
        log_message "用法: $0 {start|stop|restart|status} <version>" warn
        exit 1
        ;;
esac

exit 0