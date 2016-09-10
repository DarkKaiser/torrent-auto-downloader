package kr.co.darkkaiser.torrentad.website;

import org.jsoup.helper.StringUtil;

public enum WebSiteSearchKeywordsMode {

	INCLUDE("include"),
	EXCLUDE("exclude");
	
	private final String value;

	private WebSiteSearchKeywordsMode(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static WebSiteSearchKeywordsMode getDefault() {
		return INCLUDE;
	}
	
	public static WebSiteSearchKeywordsMode fromString(String modeValue) {
		if (modeValue == null) {
			throw new NullPointerException("modeValue");
		}
		if (StringUtil.isBlank(modeValue) == true) {
			throw new IllegalArgumentException("modeValue는 빈 문자열을 허용하지 않습니다.");
		}

		for (WebSiteSearchKeywordsMode type : WebSiteSearchKeywordsMode.values()) {
			if (modeValue.equals(type.getValue()) == true) {
				return type;
			}
	    }

		throw new IllegalArgumentException(String.format("열거형 %s에서 %s에 해당하는 값이 없습니다.", WebSiteSearchKeywordsMode.class.getSimpleName(), modeValue));
	}

}
