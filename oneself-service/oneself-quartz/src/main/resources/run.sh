#!/bin/sh

project_name=$(basename "$(pwd)")

start_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -z "$PID" ]; then
        java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar "${jar_name}" > /dev/null 2>&1 &
        sleep 2
        PID=$(pgrep -f "${jar_name}")

        if [ -n "$PID" ]; then
            printf "%-5s %-22s %-8s\n" start "$jar_name" success
        else
            printf "%-5s %-22s %-8s\n" start "$jar_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" start "$jar_name" "already running"
    fi
}

stop_project() {
    version="$1"
    jar_name="${project_name}-${version}.jar"

    if [ -z "$version" ]; then
        echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
        exit 1
    fi

    PID=$(pgrep -f "${jar_name}")
    if [ -n "$PID" ]; then
        kill -9 "$PID"
        sleep 2
        PID1=$(pgrep -f "${jar_name}")

        if [ -z "$PID1" ]; then
            printf "%-5s %-22s %-8s\n" stop "$jar_name" success
        else
            printf "%-5s %-22s %-8s\n" stop "$jar_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" stop "$jar_name" "not running"
    fi
}

restart_project() {
    version="$1"
    stop_project "$version"
    start_project "$version"
}

if [ -z "$2" ]; then
    echo "Please specify the version. Usage: $0 {start|stop|restart} <version>"
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
    *)
        echo "Usage: $0 {start|stop|restart} <version>"
        exit 1
        ;;
esac

exit 0