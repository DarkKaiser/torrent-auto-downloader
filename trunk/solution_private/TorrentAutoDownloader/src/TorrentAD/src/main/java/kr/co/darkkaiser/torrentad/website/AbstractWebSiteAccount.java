package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSiteAccount implements WebSiteAccount {

	private String id;
	private String password;

	protected AbstractWebSiteAccount(String id, String password) {
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
	public boolean valid() {
		if (StringUtil.isBlank(this.id) == true || StringUtil.isBlank(this.password) == true) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return new StringBuilder()
			.append(AbstractWebSiteAccount.class.getSimpleName())
			.append("{")
			.append("id:").append(this.id)
			.append(", password:").append(this.password)
			.append("}")
			.toString();
	}

}
