package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;

import org.jsoup.helper.StringUtil;

public class WebSiteSearchKeywordAdapter implements WebSiteSearchKeyword {

	private final WebSiteSearchKeywordType type;

	private ArrayList<String> keywords = new ArrayList<>();

	public WebSiteSearchKeywordAdapter(WebSiteSearchKeywordType type) {
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

		this.keywords.add(keyword);
	}

	@Override
	public boolean isInclusion(String text) {
		// @@@@@
		// 영어는 대소문자 구분 안함
		return true;
	}

	@Override
	public void validate() {
		if (this.keywords == null) {
			throw new NullPointerException("keywords");
		}
		if (this.keywords.size() == 0) {
			// @@@@@
//			throw new SearchKeywordException();
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
		// @@@@@ 목록 출력
		return new StringBuilder()
				.append(WebSiteSearchKeywordAdapter.class.getSimpleName())
				.append("{")
				.append("type:").append(this.type)
				.append("}")
				.toString();
	}

}
