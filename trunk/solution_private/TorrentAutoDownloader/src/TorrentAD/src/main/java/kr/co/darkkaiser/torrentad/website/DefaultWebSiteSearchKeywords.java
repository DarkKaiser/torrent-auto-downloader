package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsoup.helper.StringUtil;

public class DefaultWebSiteSearchKeywords implements WebSiteSearchKeywords {

	private final WebSiteSearchKeywordsType type;

	private final ArrayList<String> keywords = new ArrayList<>();

	public DefaultWebSiteSearchKeywords(WebSiteSearchKeywordsType type) {
		if (type == null) {
			throw new NullPointerException("type");
		}

		this.type = type;
	}

	@Override
	public void add(String keyword) {
		if (keyword == null) {
			throw new NullPointerException("keyword");
		}
		if (StringUtil.isBlank(keyword) == true) {
			throw new IllegalArgumentException("keyword는 빈 문자열을 허용하지 않습니다.");
		}

		if (this.keywords.contains(keyword) == false) {
			this.keywords.add(keyword);
		}
	}

	@Override
	public boolean isSatisfyCondition(String text) {
		// @@@@@
		if (type == WebSiteSearchKeywordsType.INCLUDE) {
			
		} else {
			
		}
		// 영어는 대소문자 구분 안함
		return true;
	}

	@Override
	public void validate() {
		if (this.keywords == null) {
			throw new NullPointerException("keywords");
		}
		if (this.keywords.size() == 0) {
			throw new EmptySearchKeywordException("검색 키워드가 등록되어 있지 않습니다.");
		}
	}

	@Override
	public boolean isValid() {
		try {
			validate();
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder()
				.append(DefaultWebSiteSearchKeywords.class.getSimpleName())
				.append("{")
				.append("type:").append(this.type)
				.append(", keywords:");

		boolean firstKeyword = true;
		Iterator<String> iterator = this.keywords.iterator();
		while (iterator.hasNext()) {
			if (firstKeyword == false) {
				sb.append("|")
				  .append(iterator.next());
			} else {
				firstKeyword = false;
				sb.append(iterator.next());
			}
		}

		sb.append("}");

		return sb.toString();
	}

}
