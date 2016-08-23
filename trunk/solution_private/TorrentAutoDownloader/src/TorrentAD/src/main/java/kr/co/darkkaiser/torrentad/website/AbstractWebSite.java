package kr.co.darkkaiser.torrentad.website;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWebSite implements WebSiteHandler, WebSiteContext {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSite.class);
	
	protected final WebSite site;

	protected WebSiteAccount account;

	protected AbstractWebSite(WebSite site) {
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;
	}

	@Override
	public void login(WebSiteAccount account) throws Exception {
		logger.info("웹사이트('{}')를 로그인합니다.", getName());

		logout0();
		login0(account);

		logger.info("웹사이트('{}')가 로그인 되었습니다.", getName());
	}
	
	protected abstract void login0(WebSiteAccount account) throws Exception;
	
	@Override
	public void logout() throws Exception {
		logger.info("웹사이트('{}')를 로그아웃합니다.", getName());

		logout0();

		logger.info("웹사이트('{}')가 로그아웃 되었습니다.", getName());
	}

	protected abstract void logout0() throws Exception;

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
	public void validate() {
		if (this.site == null) {
			throw new NullPointerException("site");
		}
		
		if (this.account == null) {
			throw new NullPointerException("account");
		}

		this.account.validate();
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(AbstractWebSite.class.getSimpleName())
			.append("{")
			.append("site:").append(this.site)
			.append(", account:").append(this.account)
			.append("}")
			.toString();
	}

}
