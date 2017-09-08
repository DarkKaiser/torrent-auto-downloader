package com.darkkaiser.torrentad.website;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultWebSiteSearchKeywords implements WebSiteSearchKeywords {

	private static final Logger logger = LoggerFactory.getLogger(DefaultWebSiteSearchKeywords.class);
	
	private final WebSiteSearchKeywordsMode mode;

	private final List<List<String>> keywords = new ArrayList<>();

	public DefaultWebSiteSearchKeywords(final WebSiteSearchKeywordsMode mode) {
		if (mode == null)
			throw new NullPointerException("mode");

		this.mode = mode;
	}

	@Override
	public void add(final String keyword) {
		if (StringUtil.isBlank(keyword) == true)
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");

		this.keywords.add(Arrays.asList(keyword.toUpperCase().split("\\+")));
	}

	@Override
	public boolean isSatisfySearchCondition(final String text) {
		if (StringUtil.isBlank(text) == true)
			throw new IllegalArgumentException("text는 빈 문자열을 허용하지 않습니다.");
		
		int index;
		String upperCaseText = text.toUpperCase();
		
		if (mode == WebSiteSearchKeywordsMode.INCLUDE) {
			for (final List<String> splitKeywords : this.keywords) {
				for (index = 0; index < splitKeywords.size(); ++index) {
					if (upperCaseText.contains(splitKeywords.get(index)) == false)
						break;
				}

				if (index == splitKeywords.size())
					return true;
			}

			return false;
		} else {
			for (final List<String> splitKeywords : this.keywords) {
				for (index = 0; index < splitKeywords.size(); ++index) {
					if (upperCaseText.contains(splitKeywords.get(index)) == false)
						break;
				}

				if (index == splitKeywords.size())
					return false;
			}

			return true;
		}
	}

	@Override
	public void validate() {
		if (this.keywords.isEmpty() == true)
			throw new EmptySearchKeywordException("검색 키워드가 등록되어 있지 않습니다.");
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (final Exception e) {
			logger.debug(null, e);
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(DefaultWebSiteSearchKeywords.class.getSimpleName())
				.append("{")
				.append("mode:").append(this.mode)
				.append(", keywords:");

		boolean firstKeyword = true;
		Iterator<List<String>> iterator = this.keywords.iterator();
		while (iterator.hasNext()) {
			if (firstKeyword == false) {
				sb.append("|")
				  .append(StringUtil.join(iterator.next(), "+"));
			} else {
				firstKeyword = false;
				sb.append(StringUtil.join(iterator.next(), "+"));
			}
		}

		sb.append("}");

		return sb.toString();
	}

}
