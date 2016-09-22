#!/bin/sh

# 서버명
SRV_NAME=TorrentAD

export LANG=ko_KR.utf-8

cd /NCIA/SafeOn2016/MSG

case "$1" in
stop)
	PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
  if [ $PROCESS_COUNT -gt 1 ]; then
    echo "The server($SRV_NAME) is running. Please check."
    exit 1
	elif [ $PROCESS_COUNT -eq 1 ]; then
		echo "Stop the Server($SRV_NAME)..."

		kill -9 `ps ax | grep -v grep | grep java | grep $SRV_NAME | awk '{print $1}'`

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
    nohup java -d64 -Xms512m -Xmx4096m -cp ./torrentad.jar kr.co.darkkaiser.torrentad.App &
  fi
;;
esac

exit 0
