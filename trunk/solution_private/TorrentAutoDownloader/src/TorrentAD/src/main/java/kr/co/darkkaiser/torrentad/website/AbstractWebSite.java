package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.website.account.Account;
import kr.co.darkkaiser.torrentad.website.account.DefaultAccount;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSiteHandler, WebSiteContext<B> {

	protected String protocol;
	
	protected String domain;

	protected Account account;
	
	@Override
	public String protocol() {
		return this.protocol;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public B protocol(String protocol) {
		if (protocol == null) {
			throw new NullPointerException("protocol");
		}

		if (StringUtil.isBlank(protocol) == true) {
			throw new IllegalArgumentException("protocol must not be empty.");
		}

		this.protocol = protocol;

		return (B) this;
	}
	
	@Override
	public String domain() {
		return this.domain;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B domain(String domain) {
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
	public Account account() {
		return this.account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B account(String id, String password) {
		this.account = new DefaultAccount(id, password);

		return (B) this;
	}
	
	@Override
	public boolean valid() {
		if (StringUtil.isBlank(this.protocol) == true || StringUtil.isBlank(this.domain) == true) {
			return false;
		}

		if (this.account.valid() == false) {
			return false;
		}

		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(AbstractWebSite.class.getSimpleName())
		   .append("(")
		   .append("protocol: ").append(this.protocol)
		   .append(", domain: ").append(this.domain)
		   .append(", account: ").append(this.account)
		   .append(")");
		
		return buf.toString();
	}

}
