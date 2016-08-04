package kr.co.darkkaiser.torrentad.website;

public abstract class AbstractWebSite<B extends AbstractWebSite<B>> implements WebSiteHandler, WebSiteContext<B> {

	protected WebSiteAccount account;
	
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
