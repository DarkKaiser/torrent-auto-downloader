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
	public void validate() {
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
			.append("account:").append(this.account)
			.append("}")
			.toString();
	}

}
