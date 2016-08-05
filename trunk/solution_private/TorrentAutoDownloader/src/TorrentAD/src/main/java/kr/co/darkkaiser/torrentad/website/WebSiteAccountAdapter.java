package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public class WebSiteAccountAdapter implements WebSiteAccount {

	private String id;
	private String password;

	protected WebSiteAccountAdapter(String id, String password) {
		validate(id, password);

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
		validate(this.id, this.password);
	}

	private void validate(String id, String password) {
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
			.append("id:").append(this.id)
			.append(", password:").append(this.password)
			.append("}")
			.toString();
	}

}
