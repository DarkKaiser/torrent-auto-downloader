package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteAccount;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteConnection;
import kr.co.darkkaiser.torrentad.website.WebSiteHandler;

// @@@@@ 테스트 클래스임, WebSiteConnection
public final class WebSiteAdapter {

	private static final Logger logger = LoggerFactory.getLogger(WebSiteAdapter.class);

	private final WebSite site;

	private final String accountId;
	private final String accountPassword;
	
	private WebSiteConnection handler;

	private final Configuration configuration;

	private final AES256Util aes256 = new AES256Util();

	public WebSiteAdapter(Configuration configuration) throws Exception {
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.configuration = configuration;

		try {
			this.site = WebSite.fromString(this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_NAME));
		} catch (RuntimeException e) {
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_TAG_WEBSITE_NAME);
			throw e;
		}

		this.accountId = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID);
		String encryptionPassword = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);

		try {
			this.accountPassword = this.aes256.decode(encryptionPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 비밀번호('{}')의 복호화 작업이 실패하였습니다.", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);
			throw e;
		}
	}
	
	public boolean login() {
		WebSiteAccount account = null;
		try {
			account = this.site.createAccount(this.accountId, this.accountPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 계정정보({})가 유효하지 않습니다.", String.format("'%s', '%s'", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID, Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD), e);
			return false;
		}

		handler = this.site.createConnection(this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));
		try {
			handler.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", this.site, e);
			return false;
		}
		
		return true;
	}
	
	public void logout() throws Exception {
		if (handler != null)
			handler.logout();
	}
	
	public WebSite getSite() {
		return this.site;
	}

	public WebSiteHandler getHandler() {
		return (WebSiteHandler) this.handler;
	}

	public WebSiteBoard getBoard(String name) {
		WebSiteBoard[] boardValues = this.site.getBoardValues();
		return this.site.getBoard(name);
//		this.board = BogoBogoBoard.fromString(name);
//		this.site.getBoard(id)
	}
	
}
