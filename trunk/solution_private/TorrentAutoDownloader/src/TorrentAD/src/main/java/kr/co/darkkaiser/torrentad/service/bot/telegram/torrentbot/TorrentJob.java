package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;

// @@@@@
public class TorrentJob {
	private ExecutorService a;
	
	private WebSite webSite;
	private WebSiteHandler webSiteHandler;

	private TorrentClient torrentClient;
	
	private WebSiteAdapter siteAdapter;
	
	private Configuration configuration;
	
	
	
	// job.search(chat_id)
	// job.get(chat_id)
	// job.list(chat_id)

	public TorrentJob(Configuration configuration) throws Exception {
		// 웹사이트 초기하
		this.siteAdapter = new WebSiteAdapter(configuration);
		this.siteAdapter.login();
		// 트랜스미션 초기화
		
		this.configuration = configuration;
		this.a = Executors.newFixedThreadPool(1);
	}

	public void search(long chatId, long requestId) throws FailedLoadBoardItemsException {
		WebSiteHandler handler = this.siteAdapter.getHandler();
		
		WebSiteBoard board = this.siteAdapter.getBoard("newmovie");
		
		

		System.out.println("############# search");
		// @@@@@
		Iterator<WebSiteBoardItem> searcha = handler.search(board, "드래곤");
		while (searcha.hasNext()) {
			WebSiteBoardItem next = searcha.next();
			System.out.println(next);
		}
	}

	public void list() throws FailedLoadBoardItemsException {
		WebSiteHandler handler = this.siteAdapter.getHandler();

		Iterator<WebSiteBoardItem> searcha = handler.list(BogoBogoBoard.ANI_ON);
		while (searcha.hasNext()) {
			WebSiteBoardItem next = searcha.next();
			System.out.println(next);
		}
	}

	public void getTorrentStatus() {
		if (a != null) {
			a.submit(new Runnable() {
				private AES256Util aes256;
				
				protected String decode(String encryption) throws Exception {
					if (this.aes256 == null)
						this.aes256 = new AES256Util();

					try {
						return this.aes256.decode(encryption);
					} catch (Exception e) {
//						logger.error("암호화 된 문자열('{}')의 복호화 작업이 실패하였습니다.", encryption);
						throw e;
					}
				}
				
				@Override
				public void run() {
					String url = TorrentJob.this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_URL);
					String id = TorrentJob.this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_ID);
					String password = "";
					try {
						password = decode(TorrentJob.this.configuration.getValue(Constants.APP_CONFIG_TAG_TORRENT_RPC_ACCOUNT_PASSWORD));
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					TorrentJob.this.torrentClient = new TransmissionRpcClient(url);
					try {
						if (TorrentJob.this.torrentClient.connect(id, password) == false) {
//						logger.warn(String.format("토렌트 서버 접속이 실패하였습니다.(Url:%s, Id:%s)", url, id));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					try {
						TorrentGetMethodResult torrent = TorrentJob.this.torrentClient.getTorrent();
						System.out.println(torrent);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
					}
					
					System.out.println("##############3333");
					// 사용자에게 전달
				}
			});
		}
	}
	


	
}
