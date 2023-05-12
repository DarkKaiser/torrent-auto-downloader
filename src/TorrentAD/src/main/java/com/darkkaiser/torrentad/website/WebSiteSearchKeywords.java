package com.darkkaiser.torrentad.website;

public interface WebSiteSearchKeywords {

	void add(final String keyword);

	boolean isSatisfySearchCondition(final String text);

	void validate();

	boolean isValid();
	
}
