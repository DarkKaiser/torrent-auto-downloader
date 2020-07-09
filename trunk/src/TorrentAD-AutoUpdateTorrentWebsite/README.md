# TorrentAD-AutoUpdateTorrentWebSite
TorrentAD 데몬의 토렌트 웹사이트 주소를 자동으로 갱신시켜 주는 프로그램

# crontab 추가내용
0 * * * * /usr/local/go-workspace/src/darkkaiser.com/auto-update-torrent-website/auto-update-torrent-website > /dev/null 2>&1

### ※ 2020년 06월 22일 TorrentQQ 사이트의 주소를 알아내는 'https://rankers.info/' 사이트에서 TorrentQQ 사이트의 주소가 업데이트 되지 않고 있어, TorrentAD 데몬에 직접 토렌트 웹사이트 주소를 갱신시켜 주는 기능을 넣음!!!