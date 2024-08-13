#!/bin/bash

GITHUB_SHA=$1

BUILD_JAR=$(ls /home/ubuntu/deploy/server-0.0.1-SNAPSHOT.jar)
JAR_NAME=$(basename $BUILD_JAR)

CURRENT_PID=$(ps aux | grep "$JAR_NAME" | grep -v grep | awk '{print $2}')

if [ -z "$CURRENT_PID" ]; then
  echo "현재 구동중인 애플리케이션이 없으므로 종료하지 않습니다."
else
  echo "현재 실행중인 애플리케이션(pid: $CURRENT_PID)을 종료합니다."
  sudo kill -15 $CURRENT_PID
  sleep 5
fi

DEPLOY_JAR="/home/ubuntu/deploy/$JAR_NAME"

echo "애플리케이션을 시작합니다."
nohup java -jar -Dspring.profiles.active=local $DEPLOY_JAR > /dev/null 2>&1 &

aws s3 rm s3://test-springboot-jar/$GITHUB_SHA.zip
