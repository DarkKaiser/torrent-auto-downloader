#!/bin/sh

# 서버명
SRV_NAME=TorrentAD

cd /usr/local/torrentad/

case "$1" in
stop)
  PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
  if [ $PROCESS_COUNT -gt 1 ]; then
    echo "$SRV_NAME 서버가 다수 실행중입니다. 확인하여 주세요."
    exit 1
  elif [ $PROCESS_COUNT -eq 1 ]; then
    echo "$SRV_NAME 서버를 중지합니다. 잠시만 기다려주세요..."

    kill `ps ax | grep -v grep | grep java | grep $SRV_NAME | awk '{print $1}'`

    index=1
    while [ $index -le 3 ]
    do
      sleep 1

      PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
      if [ $PROCESS_COUNT -eq 0 ]; then
        break
      fi

      index=$((index + 1))
    done

    PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
    if [ $PROCESS_COUNT -ne 0 ]; then
      kill -9 `ps ax | grep -v grep | grep java | grep $SRV_NAME | awk '{print $1}'`

      sleep 1

      PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
      [ $PROCESS_COUNT -ne 0 ] && echo "$SRV_NAME 서버의 중지가 실패하였습니다..." || echo "$SRV_NAME 서버가 중지되었습니다."
    else
      echo "$SRV_NAME 서버가 중지되었습니다."
    fi
  else
    echo "$SRV_NAME 서버가 실행중인 상태가 아닙니다."
    exit 1
  fi
;;

*)
  PROCESS_COUNT=`ps ax | grep -v grep | grep java | grep $SRV_NAME | wc -l`
  if [ $PROCESS_COUNT -ne 0 ]; then
    echo "$SRV_NAME 서버가 이미 실행중입니다."
  else
    echo "$SRV_NAME 서버가 실행되었습니다."
    nohup java -D$SRV_NAME -jar torrentad-1.0.jar 1>/dev/null 2>&1 &
  fi
;;
esac

exit 0
