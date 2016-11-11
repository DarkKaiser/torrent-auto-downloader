package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWebSite implements WebSiteConnection, WebSiteHandler, WebSiteContext {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSite.class);
	
	protected final WebSiteConnector siteConnector;
	
	protected final String owner;

	protected final WebSite site;

	protected WebSiteAccount account;
	
	protected AbstractWebSite(WebSiteConnector siteConnector, String owner, WebSite site) {
		if (StringUtil.isBlank(owner) == true)
			throw new IllegalArgumentException("owner는 빈 문자열을 허용하지 않습니다.");
		if (site == null)
			throw new NullPointerException("site");

		this.siteConnector = siteConnector;

		this.site = site;
		this.owner = owner;
	}
	
	@Override
	public void login(WebSiteAccount account) throws Exception {
		logger.info("{} 에서 웹사이트('{}')를 로그인합니다.", getOwner(), getName());

		logout0();
		login0(account);

		logger.info("{} 에서 웹사이트('{}')가 로그인 되었습니다.", getOwner(), getName());
	}
	
	protected abstract void login0(WebSiteAccount account) throws Exception;
	
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
	public void setAccount(WebSiteAccount account) {
		this.account = account;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(AbstractWebSite.class.getSimpleName())
			.append("{")
			.append("owner:").append(getOwner())
			.append(", site:").append(this.site)
			.append(", account:").append(getAccount())
			.append("}")
			.toString();
	}

}
