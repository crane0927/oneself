#!/bin/sh

project_name=$(basename "$(pwd)")

start_project() {
    PID=$(pgrep -f "${project_name}.jar")
    if [ -z "$PID" ]; then
        java -Xms1g -Xmx1g -XX:+HeapDumpOnOutOfMemoryError -Dlog.name="${project_name}" -jar ${project_name}.jar > /dev/null 2>&1 &
        sleep 2
        PID=$(pgrep -f "${project_name}*.jar")

        if [ -n "$PID" ]; then
            printf "%-5s %-22s %-8s\n" start "$project_name" success
        else
            printf "%-5s %-22s %-8s\n" start "$project_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" start "$project_name" "already running"
    fi
}

stop_project() {
    PID=$(pgrep -f "${project_name}.jar")
    if [ -n "$PID" ]; then
        kill -9 "$PID"
        sleep 2
        PID1=$(pgrep -f "${project_name}.jar")

        if [ -z "$PID1" ]; then
            printf "%-5s %-22s %-8s\n" stop "$project_name" success
        else
            printf "%-5s %-22s %-8s\n" stop "$project_name" failed
        fi
    else
        printf "%-5s %-22s %-8s\n" stop "$project_name" "not running"
    fi
}

restart_project() {
    stop_project
    start_project
}

case "$1" in
    start)
        start_project
        ;;
    stop)
        stop_project
        ;;
    restart)
        restart_project
        ;;
    *)
        echo "Usage: $0 {start|stop|restart}"
        exit 1
        ;;
esac

exit 0