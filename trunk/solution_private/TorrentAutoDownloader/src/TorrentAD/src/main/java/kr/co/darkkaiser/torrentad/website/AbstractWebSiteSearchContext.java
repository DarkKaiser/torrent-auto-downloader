package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractWebSiteSearchContext implements WebSiteSearchContext {

	private final WebSite site;

	private ArrayList<WebSiteSearchKeywords> searchKeywords = new ArrayList<>();

	public AbstractWebSiteSearchContext(WebSite site) {
		if (site == null) {
			throw new NullPointerException("site");
		}

		this.site = site;
	}

	@Override
	public WebSite getWebSite() {
		return this.site;
	}

	@Override
	public void addSearchKeywords(WebSiteSearchKeywords searchKeywords) throws Exception {
		if (searchKeywords == null) {
			throw new NullPointerException("searchKeywords");
		}

		this.searchKeywords.add(searchKeywords);
	}

	@Override
	public boolean isSatisfySearchCondition(String text) {
		Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.iterator();
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
		if (this.searchKeywords == null) {
			throw new NullPointerException("searchKeywords");
		}
		if (this.searchKeywords.isEmpty() == true) {
			throw new EmptySearchKeywordsException("검색 키워드가 등록되어 있지 않습니다.");
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
				.append(AbstractWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append(", searchKeywords:");

		Iterator<WebSiteSearchKeywords> iterator = this.searchKeywords.iterator();
		while (iterator.hasNext()) {
			sb.append("[")
			  .append(iterator.next())
			  .append("]");
		}

		sb.append("}");

		return sb.toString();
	}
	
}
