package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.delete_target;

import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

// @@@@@ 클래스를 삭제해도 되지 않나(TelegramTorrentBot과 합치기)??? 삭제하지 않는다면 TorrentProxy or TorrentAdapter
public class TorrentJob {
	
	public final WebSiteConnector connector;
	
	public TorrentJob(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) throws Exception {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		// @@@@@
		// 웹사이트 초기하
		this.connector = new DefaultWebSiteConnector(TorrentJob.class.getSimpleName(), configuration);
		this.connector.login();

		// 트랜스미션 초기화
	}

	public void search(long chatId, long requestId) {
		WebSiteBoard board = this.connector.getSite().getBoardByName("anion");
//		this.immediatelyTaskExecutorService.submit(new SearchBoardImmediatelyTaskAction(this.connector, board));
	}

}
