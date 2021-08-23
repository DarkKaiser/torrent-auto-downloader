package com.darkkaiser.torrentad.website;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.StringUtil;

import java.util.*;

@Slf4j
public class DefaultWebSiteSearchKeywords implements WebSiteSearchKeywords {

	private final WebSiteSearchKeywordsMode mode;

	private final List<List<String>> keywords = new ArrayList<>();

	public DefaultWebSiteSearchKeywords(final WebSiteSearchKeywordsMode mode) {
		Objects.requireNonNull(mode, "mode");

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
			log.debug(null, e);
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
