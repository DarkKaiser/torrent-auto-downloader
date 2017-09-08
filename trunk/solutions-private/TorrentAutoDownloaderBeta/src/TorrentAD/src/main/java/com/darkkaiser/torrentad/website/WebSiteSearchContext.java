package com.darkkaiser.torrentad.website;

public interface WebSiteSearchContext {

	WebSite getWebSite();

	void setBoardName(final String name);

	long getLatestDownloadBoardItemIdentifier();

	void setLatestDownloadBoardItemIdentifier(final long identifier);

	void addSearchKeywords(final WebSiteSearchKeywordsType type, final WebSiteSearchKeywords searchKeywords);

	boolean isSatisfySearchCondition(final WebSiteSearchKeywordsType type, final String text);

	void validate();

	boolean isValid();

}
