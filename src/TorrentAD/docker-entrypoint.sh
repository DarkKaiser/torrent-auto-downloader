#!/bin/bash
set -e

APP_PATH=/usr/local/app/
APP_JAR_FILE=/usr/local/app/torrentad-1.1.0.jar

LATEST_APP_JAR_FILE=/docker-entrypoint/dist/torrentad-1.1.0.jar

if [ -f "$LATEST_APP_JAR_FILE" ]; then
  mv -f $LATEST_APP_JAR_FILE $APP_PATH
  chown +1000:staff $APP_JAR_FILE
fi

exec "$@"
