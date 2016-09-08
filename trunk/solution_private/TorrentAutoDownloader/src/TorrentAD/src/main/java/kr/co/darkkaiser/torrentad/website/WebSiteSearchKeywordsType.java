package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public enum WebSiteSearchKeywordsType {

	INCLUDE("include"),
	EXCLUDE("exclude");
	
	private final String value;

	private WebSiteSearchKeywordsType(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static WebSiteSearchKeywordsType getDefault() {
		return INCLUDE;
	}
	
	public static WebSiteSearchKeywordsType fromString(String value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		if (StringUtil.isBlank(value) == true) {
			throw new IllegalArgumentException("value은 빈 문자열을 허용하지 않습니다.");
		}

		for (WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (value.equals(type.getValue()) == true) {
				return type;
			}
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSiteSearchKeywordsType.class.getSimpleName(), value));
	}

}
