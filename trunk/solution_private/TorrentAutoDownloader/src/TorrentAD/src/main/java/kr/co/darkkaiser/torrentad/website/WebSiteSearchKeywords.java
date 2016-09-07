package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchKeywords {

	void add(String keyword);

	boolean isSatisfyCondition(String text);

	void validate();

	boolean isValid();
	
}
