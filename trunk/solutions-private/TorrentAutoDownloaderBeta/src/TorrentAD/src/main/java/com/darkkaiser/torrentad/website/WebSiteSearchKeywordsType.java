package com.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public enum WebSiteSearchKeywordsType {

	TITLE("title", false),

	FILE("file", true);

	private final String value;
	
	private final boolean allowEmpty;

	private WebSiteSearchKeywordsType(String value, boolean allowEmpty) {
		this.value = value;
		this.allowEmpty = allowEmpty;
	}

	public String getValue() {
		return this.value;
	}
	
	public boolean allowEmpty() {
		return this.allowEmpty;
	}

	public static WebSiteSearchKeywordsType fromString(String typeValue) {
		if (typeValue == null)
			throw new NullPointerException("typeValue");
		if (StringUtil.isBlank(typeValue) == true)
			throw new IllegalArgumentException("typeValue는 빈 문자열을 허용하지 않습니다.");

		for (WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (typeValue.equals(type.getValue()) == true)
				return type;
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSiteSearchKeywordsType.class.getSimpleName(), typeValue));
	}

}
