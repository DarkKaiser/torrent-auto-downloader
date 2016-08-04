package kr.co.darkkaiser.torrentad.website;

import kr.co.darkkaiser.torrentad.website.account.Account;
import kr.co.darkkaiser.torrentad.website.account.DefaultAccount;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSiteHandler, WebSiteContext<B> {

	protected Account account;
	
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
