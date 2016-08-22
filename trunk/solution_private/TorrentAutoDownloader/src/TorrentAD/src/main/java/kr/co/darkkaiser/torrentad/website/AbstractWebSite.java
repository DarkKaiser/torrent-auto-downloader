package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSite<B> {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebSite.class);
	
	protected final String name;

	protected WebSiteAccount account;

	protected AbstractWebSite(String name) {
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (StringUtil.isBlank(name) == true) {
			throw new IllegalArgumentException("name must not be empty.");
		}

		this.name = name;
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
		return this.name;
	}

	@Override
	public WebSiteAccount getAccount() {
		return this.account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setAccount(WebSiteAccount account) {
		this.account = account;

		return (B) this;
	}
	
	@Override
	public void validate() {
		if (name == null) {
			throw new NullPointerException("name");
		}

		if (StringUtil.isBlank(name) == true) {
			throw new IllegalArgumentException("name must not be empty.");
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
			.append("name:").append(this.name)
			.append(", account:").append(this.account)
			.append("}")
			.toString();
	}

}
