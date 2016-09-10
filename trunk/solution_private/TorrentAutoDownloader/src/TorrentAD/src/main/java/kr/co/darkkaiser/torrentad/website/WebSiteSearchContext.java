package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchContext {

	WebSite getWebSite();

	void setBoardName(String name) throws Exception;

	void addSearchKeywords(WebSiteSearchKeywords searchKeywords) throws Exception;

	boolean isSatisfySearchCondition(String text);

	void validate();

	boolean isValid();

}
