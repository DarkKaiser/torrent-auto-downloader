package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchContext {

	WebSite getWebSite();

	void setBoardName(String name) throws Exception;

	long getLatestDownloadBoardItemIdentifier();

	void setLatestDownloadBoardItemIdentifier(long identifier) throws Exception;

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords) throws Exception;

	boolean isSatisfySearchCondition(WebSiteSearchKeywordsType type, String text);

	void validate();

	boolean isValid();

}
