#!/bin/sh

project_name=$(basename "$(pwd)")

log_message() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') $1"
}

start_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart|status} <version>"
        exit 1
    fi

    if [ ! -f "${jar_name}" ]; then
        echo "Jar file ${jar_name} does not exist. Please check the version or file path."
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -z "$PID" ]; then
        java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar "${jar_name}" > /dev/null 2>&1 &
        sleep 2
        PID=$(pgrep -f "${jar_name}")

        if [ -n "$PID" ]; then
            log_message "start ${jar_name} success"
        else
            log_message "start ${jar_name} failed"
        fi
    else
        log_message "start ${jar_name} already running (PID: $PID)"
    fi
}

stop_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart|status} <version>"
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        kill -9 "$PID"
        sleep 2
        PID1=$(pgrep -f "${jar_name}")

        if [ -z "$PID1" ]; then
            log_message "stop ${jar_name} success"
        else
            log_message "stop ${jar_name} failed"
        fi
    else
        log_message "stop ${jar_name} not running"
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
    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        log_message "status ${jar_name} running (PID: $PID)"
    else
        log_message "status ${jar_name} not running"
    fi
}

if [ -z "$2" ]; then
    echo "Please specify the version. Usage: $0 {start|stop|restart|status} <version>"
    exit 1
fi

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
        echo "Usage: $0 {start|stop|restart|status} <version>"
        exit 1
        ;;
esac

exit 0