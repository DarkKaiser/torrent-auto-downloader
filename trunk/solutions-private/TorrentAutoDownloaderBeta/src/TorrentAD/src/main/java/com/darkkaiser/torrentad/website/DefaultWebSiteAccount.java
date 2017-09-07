package com.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebSiteAccount implements WebSiteAccount {

	private static final Logger logger = LoggerFactory.getLogger(DefaultWebSiteAccount.class);
	
	private final String id;
	private final String password;

	protected DefaultWebSiteAccount(String id, String password) {
		validate0(id, password);

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
		validate0(this.id, this.password);
	}

	private void validate0(String id, String password) {
		if (id == null)
			throw new NullPointerException("id");
		if (StringUtil.isBlank(id) == true)
			throw new IllegalArgumentException("id는 빈 문자열을 허용하지 않습니다.");
		if (password == null)
			throw new NullPointerException("password");
		if (StringUtil.isBlank(password) == true)
			throw new IllegalArgumentException("password는 빈 문자열을 허용하지 않습니다.");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			logger.debug(null, e);
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
			.append(DefaultWebSiteAccount.class.getSimpleName())
			.append("{")
			.append(", id:").append(this.id)
			.append("}")
			.toString();
	}

}
