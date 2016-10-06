package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.net.torrent.TorrentClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.TransmissionRpcClient;
import kr.co.darkkaiser.torrentad.net.torrent.transmission.methodresult.TorrentGetMethodResult;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.DefaultWebSiteConnector;
import kr.co.darkkaiser.torrentad.website.FailedLoadBoardItemsException;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;
import kr.co.darkkaiser.torrentad.website.WebSiteConnector;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;
import kr.co.darkkaiser.torrentad.website.impl.bogobogo.BogoBogoBoard;

// @@@@@
public class TorrentJob {
	
	private static final Logger logger = LoggerFactory.getLogger(TorrentJob.class);

	private ExecutorService a;
	
	private TorrentClient torrentClient;
	
	private final WebSiteConnector connector;
	
	private final Configuration configuration;
	
	public TorrentJob(Configuration configuration) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;
		
		// @@@@@
		// 웹사이트 초기하
		this.connector = new DefaultWebSiteConnector(configuration);
		this.connector.login();
		
		// 트랜스미션 초기화
		
		this.a = Executors.newFixedThreadPool(1);
	}

	public void search(long chatId, long requestId) throws FailedLoadBoardItemsException {
		WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();
		
//		WebSiteBoard[] boardValues = this.site.getBoardValues();
//		return this.site.getBoard(name);
		WebSiteBoard board = this.connector.getSite().getBoard("newmovie");
		
		

		System.out.println("############# search");
		// @@@@@
		Iterator<WebSiteBoardItem> searcha = handler.search(board, "드래곤");
		while (searcha.hasNext()) {
			WebSiteBoardItem next = searcha.next();
			System.out.println(next);
		}
	}

	public void list() throws FailedLoadBoardItemsException {
		WebSiteHandler handler = (WebSiteHandler) this.connector.getConnection();

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
