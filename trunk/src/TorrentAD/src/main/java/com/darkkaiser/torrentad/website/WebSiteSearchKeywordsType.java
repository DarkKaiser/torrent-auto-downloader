package com.darkkaiser.torrentad.website;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jsoup.helper.StringUtil;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum WebSiteSearchKeywordsType {

	TITLE("title", false),

	FILE("file", true);

	private final String value;

	@Accessors(fluent = true)
	private final boolean allowEmpty;

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
