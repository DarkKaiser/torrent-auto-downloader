package com.darkkaiser.torrentad.website;

import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;

import java.util.Objects;

@Slf4j
@Getter
@Accessors(fluent = true)
public class DefaultWebSiteAccount implements WebSiteAccount {

	private final String id;
	private final String password;

	protected DefaultWebSiteAccount(final String id, final String password) {
		validate0(id, password);

		this.id = id;
		this.password = password;
	}

	@Override
	public void validate() {
		validate0(this.id, this.password);
	}

	private void validate0(final String id, final String password) {
		Objects.requireNonNull(id, "id");
		if (StringUtil.isBlank(id) == true)
			throw new IllegalArgumentException("id는 빈 문자열을 허용하지 않습니다.");

		Objects.requireNonNull(password, "password");
		if (StringUtil.isBlank(password) == true)
			throw new IllegalArgumentException("password는 빈 문자열을 허용하지 않습니다.");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (final Exception e) {
			log.debug(null, e);
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		return DefaultWebSiteAccount.class.getSimpleName() +
				"{" +
				"id:" + this.id +
				"}";
	}

}
