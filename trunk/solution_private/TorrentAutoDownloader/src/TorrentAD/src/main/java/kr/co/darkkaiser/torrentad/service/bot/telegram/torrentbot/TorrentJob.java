package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import kr.co.darkkaiser.torrentad.service.ad.task.immediately.ImmediatelyTaskExecutorService;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.GetTorrentImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.immediatelytaskaction.SearchBoardImmediatelyTaskAction;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;

// @@@@@ 클래스를 삭제해도 되지 않나(TelegramTorrentBot과 합치기)??? 삭제하지 않는다면 TorrentProxy or TorrentAdapter
public class TorrentJob {
	
	private final ImmediatelyTaskExecutorService immediatelyTaskExecutorService;

	private TorrentClient torrentClient;
	
	public final WebSiteConnector connector;
	
	private final Configuration configuration;

	private AES256Util aes256;

	public TorrentJob(ImmediatelyTaskExecutorService immediatelyTaskExecutorService, Configuration configuration) throws Exception {
		if (immediatelyTaskExecutorService == null)
			throw new NullPointerException("immediatelyTaskExecutorService");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		this.immediatelyTaskExecutorService = immediatelyTaskExecutorService;

		// @@@@@
		// 웹사이트 초기하
		this.connector = new DefaultWebSiteConnector(TorrentJob.class.getSimpleName(), configuration);
		this.connector.login();

		// 트랜스미션 초기화
	}

	public void search(long chatId, long requestId) {
		WebSiteBoard board = this.connector.getSite().getBoard("anion");
		this.immediatelyTaskExecutorService.submit(new SearchBoardImmediatelyTaskAction(this.connector, board));
	}

	public void getTorrentStatus() {
		String url = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
		String id = this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
		String password = "";
		try {
			password = decode(this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.torrentClient = new TransmissionRpcClient(url);
		try {
			if (this.torrentClient.connect(id, password) == false) {
//			logger.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		this.immediatelyTaskExecutorService.submit(new GetTorrentImmediatelyTaskAction(this.torrentClient));
	}

	protected String decode(String encryption) throws Exception {
		if (this.aes256 == null)
			this.aes256 = new AES256Util();

		try {
			return this.aes256.decode(encryption);
		} catch (Exception e) {
//			logger.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
			throw e;
		}
	}

}
