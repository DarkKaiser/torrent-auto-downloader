package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSite, WebSiteContext<B> {

	protected String domain;

	protected Account account;
	
	@Override
	public String getDomain() {
		return this.domain;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setDomain(String domain) {
		if (domain == null) {
			throw new NullPointerException("domain");
		}

		if (StringUtil.isBlank(domain) == true) {
			throw new IllegalArgumentException("domain must not be empty.");
		}

		this.domain = domain;

		return (B) this;
	}

	@Override
	public Account getAccount() {
		return this.account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setAccount(Account account) {
		if (account == null) {
			throw new NullPointerException("account");
		}

		this.account = account;

		return (B) this;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(AbstractWebSite.class.getSimpleName())
		   .append("(")
		   .append("domain: ").append(this.getDomain())
		   .append(", account: ").append(this.getAccount())
		   .append(")");
		
		return buf.toString();
	}

}
