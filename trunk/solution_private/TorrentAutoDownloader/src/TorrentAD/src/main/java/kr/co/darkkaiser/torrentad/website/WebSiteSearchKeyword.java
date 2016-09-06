package kr.co.darkkaiser.torrentad.website;

public interface WebSiteSearchKeyword {

	// @@@@@
	void add(String item);
	
	boolean isInclusion(String text);
	
	boolean isValid();
	
}
