package kr.co.darkkaiser.torrentad.website;

public abstract class AbstractWebSiteSearchContext implements WebSiteSearchContext {

	private final WebSite site;

	public AbstractWebSiteSearchContext(WebSite site) {
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;
	}

	@Override
	public WebSite getWebSite() {
		return this.site;
	}

	@Override
	public void validate() {
		if (this.site == null) {
			throw new NullPointerException("site");
		}
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
				.append(AbstractWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}")
				.toString();
	}
	
}
