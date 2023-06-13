#!/bin/bash
set -e

APP_JAR_FILE=/docker-entrypoint/dist/torrentad-1.1.0.jar

if [ -f "$APP_JAR_FILE" ]; then
  mv -f $APP_JAR_FILE /usr/local/app/
fi

exec "$@"
