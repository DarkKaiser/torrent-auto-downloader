#!/bin/sh

# 서버명
SRV_NAME=TorrentAD

case "$1" in
stop)
  PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
  if [ $PROCESS_COUNT -gt 1 ]; then
    echo "The server($SRV_NAME) is running. Please check."
    exit 1
  elif [ $PROCESS_COUNT -eq 1 ]; then
    echo "Stop the Server($SRV_NAME)..."

    kill `ps ax | grep -v grep | grep java | grep $SRV_NAME | awk '{print $1}'`

    sleep 1
	
    PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
    [ $PROCESS_COUNT -ne 0 ] && echo "Stop the server($SRV_NAME) failed..." || echo "The server($SRV_NAME) has been stopped."
  else
    echo "The server($SRV_NAME) is not running."
    exit 1
  fi
;;

*)
  PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
  if [ $PROCESS_COUNT -ne 0 ]
  then
    echo "The server($SRV_NAME) is already running."
  else
    echo "The server($SRV_NAME) is running."
    nohup java -D$SRV_NAME -jar torrentad-1.0.jar 1>/dev/null 2>&1 &
  fi
;;
esac

exit 0
