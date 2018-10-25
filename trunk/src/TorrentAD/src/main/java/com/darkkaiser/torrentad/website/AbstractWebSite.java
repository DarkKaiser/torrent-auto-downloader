package com.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Objects;

public abstract class AbstractWebSite implements WebSiteConnection, WebSiteHandler, WebSiteContext {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSite.class);

	protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0";

	protected static final int URL_CONNECTION_TIMEOUT_LONG_MILLISECOND = 60 * 1000;
	protected static final int URL_CONNECTION_TIMEOUT_SHORT_MILLISECOND = 15 * 1000;

	protected static final int MAX_SEARCH_RESULT_DATA_COUNT = 100;

	protected final WebSiteConnector siteConnector;
	
	protected final String owner;

	protected final WebSite site;

	protected WebSiteAccount account;

	// 다운로드 받은 파일이 저장되는 위치
	protected final String downloadFileWriteLocation;

	protected AbstractWebSite(final WebSiteConnector siteConnector, final String owner, final WebSite site, final String downloadFileWriteLocation) {
		if (StringUtil.isBlank(owner) == true)
			throw new IllegalArgumentException("owner는 빈 문자열을 허용하지 않습니다.");
		if (StringUtil.isBlank(downloadFileWriteLocation) == true)
			throw new IllegalArgumentException("downloadFileWriteLocation은 빈 문자열을 허용하지 않습니다.");

		Objects.requireNonNull(site, "site");

		this.siteConnector = siteConnector;

		this.site = site;
		this.owner = owner;

		if (downloadFileWriteLocation.endsWith(File.separator) == true) {
			this.downloadFileWriteLocation = downloadFileWriteLocation;
		} else {
			this.downloadFileWriteLocation = String.format("%s%s", downloadFileWriteLocation, File.separator);
		}
	}
	
	@Override
	public void login(final WebSiteAccount account) throws Exception {
		logger.info("{} 에서 웹사이트('{}')를 로그인합니다.", getOwner(), getName());

		logout0();
		login0(account);

		logger.info("{} 에서 웹사이트('{}')가 로그인 되었습니다.", getOwner(), getName());
	}
	
	protected abstract void login0(final WebSiteAccount account) throws Exception;
	
	@Override
	public void logout() throws Exception {
		logger.info("{} 에서 웹사이트('{}')를 로그아웃합니다.", getOwner(), getName());

		logout0();

		logger.info("{} 에서 웹사이트('{}')가 로그아웃 되었습니다.", getOwner(), getName());
	}

	protected abstract void logout0() throws Exception;

	public WebSiteConnector getSiteConnector() {
		return this.siteConnector;
	}

	protected String getOwner() {
		return this.owner;
	}

	@Override
	public String getName() {
		return this.site.getName();
	}

	@Override
	public WebSiteAccount getAccount() {
		return this.account;
	}

	@Override
	public void setAccount(final WebSiteAccount account) {
		this.account = account;
	}

	protected String trimString(final String value) {
		int start = 0;
		int length = value.length();

		// Character.isSpaceChar() : trim()으로 제거되지 않는 공백(no-break space=줄바꿈없는공백)을 제거하기 위해 사용한다.

		while ((start < length) && (Character.isSpaceChar(value.charAt(start)) == true)) {
			start++;
		}
		while ((start < length) && (Character.isSpaceChar(value.charAt(length - 1)) == true)) {
			length--;
		}

		return ((start > 0) || (length < value.length())) ? value.substring(start, length) : value;
	}

	@Override
	public String toString() {
		return AbstractWebSite.class.getSimpleName() +
				"{" +
				"owner:" + getOwner() +
				", site:" + this.site +
				", account:" + getAccount() +
				", downloadFileWriteLocation:" + this.downloadFileWriteLocation +
				"}";
	}

}
