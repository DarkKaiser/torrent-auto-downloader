package com.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

import java.util.Objects;

public enum WebSiteSearchKeywordsType {

	TITLE("title", false),

	FILE("file", true);

	private final String value;
	
	private final boolean allowEmpty;

	WebSiteSearchKeywordsType(final String value, final boolean allowEmpty) {
		this.value = value;
		this.allowEmpty = allowEmpty;
	}

	public String getValue() {
		return this.value;
	}
	
	public boolean allowEmpty() {
		return this.allowEmpty;
	}

	public static WebSiteSearchKeywordsType fromString(final String typeValue) {
		Objects.requireNonNull(typeValue, "typeValue");

		if (StringUtil.isBlank(typeValue) == true)
			throw new IllegalArgumentException("typeValue는 빈 문자열을 허용하지 않습니다.");

		for (final WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (typeValue.equals(type.getValue()) == true)
				return type;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSiteSearchKeywordsType.class.getSimpleName(), typeValue));
	}

}
