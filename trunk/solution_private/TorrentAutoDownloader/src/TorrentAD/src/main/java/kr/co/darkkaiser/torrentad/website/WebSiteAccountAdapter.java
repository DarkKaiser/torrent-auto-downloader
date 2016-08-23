package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public class WebSiteAccountAdapter implements WebSiteAccount {

	private final WebSite site;

	private final String id;
	private final String password;

	protected WebSiteAccountAdapter(WebSite site, String id, String password) {
		validate(site, id, password);

		this.site = site;

		this.id = id;
		this.password = password;
	}

	@Override
	public String id() {
		return this.id;
	}

	@Override
	public String password() {
		return this.password;
	}
	
	@Override
	public void validate() {
		validate(this.site, this.id, this.password);
	}

	private void validate(WebSite site, String id, String password) {
		if (site == null) {
			throw new NullPointerException("site");
		}
		if (id == null) {
			throw new NullPointerException("id");
		}
		if (StringUtil.isBlank(id) == true) {
			throw new IllegalArgumentException("id must not be empty.");
		}
		if (password == null) {
			throw new NullPointerException("password");
		}
		if (StringUtil.isBlank(password) == true) {
			throw new IllegalArgumentException("password must not be empty.");
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
			.append(WebSiteAccountAdapter.class.getSimpleName())
			.append("{")
			.append("site:").append(this.site)
			.append(", id:").append(this.id)
			.append(", password:").append(this.password)
			.append("}")
			.toString();
	}

}
