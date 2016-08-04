package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public final class Account {

	private String id;
	private String password;

	public Account(String id, String password) {
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

	public String id() {
		return this.id;
	}

	public String password() {
		return this.password;
	}
	
	public boolean valid() {
		if (StringUtil.isBlank(this.id()) == true || StringUtil.isBlank(this.password()) == true) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(Account.class.getSimpleName())
		   .append("(")
		   .append("id: ").append(this.id())
		   .append(", password: ").append(this.password())
		   .append(")");
		
		return buf.toString();
	}

}
