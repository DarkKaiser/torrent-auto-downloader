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
	
	public static WebSiteSearchKeywordsType fromString(String type) {
		if (type == null) {
			throw new NullPointerException("type");
		}
		if (StringUtil.isBlank(type) == true) {
			throw new IllegalArgumentException("type은 빈 문자열을 허용하지 않습니다.");
		}

		if (type.equals(INCLUDE.getValue()) == true) {
			return INCLUDE;
		} else if (type.equals(EXCLUDE.getValue()) == true) {
			return EXCLUDE;
		}

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSiteSearchKeywordsType.class.getSimpleName(), type));
	}

}
