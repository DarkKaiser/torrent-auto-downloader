package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.config.Configuration;
import kr.co.darkkaiser.torrentad.util.crypto.AES256Util;

public class DefaultWebSiteConnector implements WebSiteConnector {

	private static final Logger logger = LoggerFactory.getLogger(DefaultWebSiteConnector.class);

	private final String owner;

	private final WebSite site;

	private WebSiteConnection connection;

	private final String accountId;
	private final String accountPassword;

	private final Configuration configuration;

	private final AES256Util aes256 = new AES256Util();

	public DefaultWebSiteConnector(String owner, Configuration configuration) throws Exception {
		if (StringUtil.isBlank(owner) == true)
			throw new IllegalArgumentException("owner는 빈 문자열을 허용하지 않습니다.");
		if (configuration == null)
			throw new NullPointerException("configuration");

		this.owner = owner;
		this.configuration = configuration;

		// 웹사이트 정보를 읽어들인다.
		try {
			this.site = WebSite.fromString(this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_NAME));
		} catch (RuntimeException e) {
			logger.error("등록된 웹사이트의 이름('{}')이 유효하지 않습니다.", Constants.APP_CONFIG_TAG_WEBSITE_NAME);
			throw e;
		}

		// 웹사이트 Connection 객체를 생성한다.
		this.connection = this.site.createConnection(getOwner(), this.configuration.getValue(Constants.APP_CONFIG_TAG_DOWNLOAD_FILE_WRITE_LOCATION));

		// 웹사이트 접속 계정정보를 읽어들인다.
		this.accountId = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID);
		String encryptionPassword = this.configuration.getValue(Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);

		try {
			this.accountPassword = this.aes256.decode(encryptionPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 비밀번호('{}')의 복호화 작업이 실패하였습니다.", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD);
			throw e;
		}
	}

	@Override
	public boolean login() {
		WebSiteAccount account = null;
		try {
			account = this.site.createAccount(this.accountId, this.accountPassword);
		} catch (Exception e) {
			logger.error("등록된 웹사이트의 계정정보({})가 유효하지 않습니다.", String.format("'%s', '%s'", Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_ID, Constants.APP_CONFIG_TAG_WEBSITE_ACCOUNT_PASSWORD), e);
			return false;
		}

		try {
			this.connection.login(account);
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그인이 실패하였습니다.", this.site, e);
			return false;
		}

		return true;
	}
	
	@Override
	public boolean logout() {
		try {
			this.connection.logout();
		} catch (Exception e) {
			logger.error("웹사이트('{}') 로그아웃이 실패하였습니다.", this.site, e);
			return false;
		}

		return true;
	}
	
	@Override
	public boolean isLogin() {
		return this.connection.isLogin();
	}

	@Override
	public String getOwner() {
		return this.owner;
	}

	@Override
	public final WebSite getSite() {
		return this.site;
	}

	@Override
	public final WebSiteConnection getConnection() {
		return this.connection;
	}

}
