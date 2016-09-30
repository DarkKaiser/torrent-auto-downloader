package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.concurrent.ExecutorService;

import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

// @@@@@
public class TorrentJob {
	private ExecutorService a;
	
	private WebSite webSite;
	private WebSiteHandler webSiteHandler;
	
	private TorrentClient transmissionRpcClient;
	// job.search(chat_id)
	// job.get(chat_id)
	// job.list(chat_id)

	public TorrentJob() {
		// 웹사이트 초기하
		// 트랜스미션 초기화
	}

	public void search() {
		// @@@@@
		if (a != null) {
			a.submit(new Runnable() {
				@Override
				public void run() {
					// 토렌트 상태 추출
//					transmissionRpcClient.getTorrent()
					
					// 사용자에게 전달
				}
			});
			a.submit(new Runnable() {
				@Override
				public void run() {
					// 토렌트 목록 조회
//					webSite.createHandler(downloadFileWriteLocation)
					
					// 사용자에게 전달
				}
			});
		}
		
	}
	
	public void list() {
		
	}
	
	public void get() {
		
	}
	
}
