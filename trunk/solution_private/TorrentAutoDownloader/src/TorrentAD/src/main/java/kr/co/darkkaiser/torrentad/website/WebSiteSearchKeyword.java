package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchKeyword {

	void addKeyword(String keyword);

	boolean isSatisfyCondition(String text);

	void validate();

	boolean isValid();
	
}
