package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jsoup.helper.StringUtil;

public abstract class AbstractWebSiteSearchContext implements WebSiteSearchContext {

	private final WebSite site;

	private Map<WebSiteSearchKeywordsType, List<WebSiteSearchKeywords>> searchKeywords = new HashMap<>();

	public AbstractWebSiteSearchContext(WebSite site) {
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;

		for (WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			this.searchKeywords.put(type, new ArrayList<>());
	    }
	}

	@Override
	public WebSite getWebSite() {
		return this.site;
	}

	@Override
	public void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords) throws Exception {
		if (type == null) {
			throw new NullPointerException("type");
		}
		if (searchKeywords == null) {
			throw new NullPointerException("searchKeywords");
		}

		this.searchKeywords.get(type).add(searchKeywords);
	}

	@Override
	public boolean isSatisfySearchCondition(WebSiteSearchKeywordsType type, String text) {
		if (type == null) {
			throw new NullPointerException("type");
		}
		if (StringUtil.isBlank(text) == true) {
			throw new IllegalArgumentException("text는 빈 문자열을 허용하지 않습니다.");
		}

		// @@@@@
		Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.get(type).iterator();
		while (iterator.hasNext()) {
			if (iterator.next().isSatisfySearchCondition(text) == false) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void validate() {
		if (this.site == null) {
			throw new NullPointerException("site");
		}
		
		// @@@@@
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
		// @@@@@
		StringBuilder sb = new StringBuilder()
				.append(AbstractWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append(", searchKeywords:");

		boolean firstKeywords = true;
		for (WebSiteSearchKeywordsType type : WebSiteSearchKeywordsType.values()) {
			if (firstKeywords == false) {
				sb.append(", ")
				  .append(type.getValue())
				  .append("[");
			} else {
				firstKeywords = false;
				sb.append(type.getValue())
				  .append("[");
			}

			Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.get(type).iterator();
			while (iterator.hasNext()) {
				sb.append("<")
				  .append(iterator.next())
				  .append(">");
			}
			
			sb.append("]");
	    }

		sb.append("}");

		return sb.toString();
	}

}
