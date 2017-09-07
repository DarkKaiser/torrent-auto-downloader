package com.darkkaiser.torrentad.website;

public interface WebSiteSearchContext {

	WebSite getWebSite();

	void setBoardName(String name);

	long getLatestDownloadBoardItemIdentifier();

	void setLatestDownloadBoardItemIdentifier(long identifier);

	void addSearchKeywords(WebSiteSearchKeywordsType type, WebSiteSearchKeywords searchKeywords);

	boolean isSatisfySearchCondition(WebSiteSearchKeywordsType type, String text);

	void validate();

	boolean isValid();

}
