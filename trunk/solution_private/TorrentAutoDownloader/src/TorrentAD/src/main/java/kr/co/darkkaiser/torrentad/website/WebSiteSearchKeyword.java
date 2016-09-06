package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchKeyword {

	void add(String keyword);
	
	// @@@@@
	boolean isInclusion(String text);

	void validate();

	boolean isValid();
	
}
