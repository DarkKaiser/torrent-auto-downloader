package kr.co.darkkaiser.torrentad.overwatch;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.config.ConfigurationManager;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSite;
import kr.co.darkkaiser.torrentad.website.BogoBogoWebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

public class OverWatcher {

	private static final Logger logger = LoggerFactory.getLogger(OverWatcher.class);

	private String configFilePath;

	private WebSiteHandler handler;

	public OverWatcher(String configFilePath) {
		if (configFilePath == null) {
			throw new NullPointerException("configFilePath");
		}

		if (StringUtil.isBlank(configFilePath) == true) {
			throw new IllegalArgumentException("configFilePath must not be empty.");
		}

		this.configFilePath = configFilePath;
	}

	public boolean start() {
		// @@@@@
		ConfigurationManager cm = null;
		try {
			cm = new ConfigurationManager(this.configFilePath);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		} finally {
			cm.dispose();
		}

		// @@@@@
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
		// TODO Auto-generated method stub
		
		// @@@@@
	}

}
