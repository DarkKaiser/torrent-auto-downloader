package kr.co.darkkaiser.torrentad.website;

import kr.co.darkkaiser.torrentad.website.account.Account;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSiteHandler, WebSiteContext<B> {

	protected Account account;
	
	@Override
	public Account getAccount() {
		return this.account;
	}

	@Override
	@SuppressWarnings("unchecked")
	public B setAccount(Account account) {
		this.account = account;

		return (B) this;
	}
	
	@Override
	public boolean valid() {
		if (this.account.valid() == false) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(AbstractWebSite.class.getSimpleName())
			.append("{")
			.append("account:").append(this.account)
			.append("}")
			.toString();
	}

}
