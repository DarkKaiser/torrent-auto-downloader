package kr.co.darkkaiser.torrentad.overwatch;

import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public final class OverWatchManager extends TimerTask {

	private static final Logger logger = LoggerFactory.getLogger(OverWatchManager.class);

	private WebSiteHandler handler;
	
	private ExecutorService executorService;

	private ConfigurationManager configurationManager;

	public OverWatchManager(ConfigurationManager configurationManager) {
		if (configurationManager == null) {
			throw new NullPointerException("configurationManager");
		}

		this.configurationManager = configurationManager;
	}

	public boolean start() {
		// @@@@@
		this.executorService = Executors.newFixedThreadPool(1);
		
		this.executorService.shutdown();
		
		BogoBogoWebSite l = new BogoBogoWebSite();

		try {
			l.login(new BogoBogoWebSiteAccount("darkkaiser", "DreamWakuWaku78@"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* 반환 */ l.search(/* 검색정보 */);
		/* 반환받은 정보를 이용해서 다운로드 */
		/* 결과정보*/l.download(/*다운로드정보*/);
		l.upload(/*결과정보*/);
		
		try {
			l.logout();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	public void stop() {
		// @@@@@
	}
	
	public void add() {
		// @@@@@
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// @@@@@
//		this.executorService.submit(task);
	}

}
