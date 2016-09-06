package kr.co.darkkaiser.torrentad.website;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class AbstractWebSiteSearchContext implements WebSiteSearchContext {

	private final WebSite site;
	
	// @@@@@
	private ArrayList<WebSiteSearchKeywordAdapter> searchKeyword;
	private ArrayList<WebSiteSearchKeywordAdapter> excludeSearchKeyword;

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
	
	// @@@@@
	private void add(WebSiteSearchKeywordAdapter searchCondition) {
		this.searchKeyword.add(searchCondition);
	}
	
	// @@@@@
	private void addExclude(WebSiteSearchKeywordAdapter searchCondition) {
		this.excludeSearchKeyword.add(searchCondition);
	}
	
	// @@@@@
	private boolean isInclusion(String text) {
		Iterator<WebSiteSearchKeywordAdapter> iterator = this.searchKeyword.iterator();
		while (iterator.hasNext()) {
			WebSiteSearchKeywordAdapter next = iterator.next();
			if (next.isInclusion(text) == false) {
				return false;
			}
		}
		
		Iterator<WebSiteSearchKeywordAdapter> iterator2 = this.excludeSearchKeyword.iterator();
		while (iterator2.hasNext()) {
			WebSiteSearchKeywordAdapter next2 = iterator2.next();
			if (next2.isInclusion(text) == true) {
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
		return new StringBuilder()
				.append(AbstractWebSiteSearchContext.class.getSimpleName())
				.append("{")
				.append("site:").append(this.site)
				.append("}")
				.toString();
	}
	
}
