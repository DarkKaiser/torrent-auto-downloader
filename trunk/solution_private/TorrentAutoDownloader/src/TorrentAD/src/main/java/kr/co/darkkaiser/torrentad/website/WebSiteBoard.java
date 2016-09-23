package kr.co.darkkaiser.torrentad.website;

public interface WebSiteBoard {

	String getName();
	
	String getDescription();
	
	String getURL();
	
	boolean hasCategoryInfo();
	
	String getDefaultRegistDateFormatString();
	
	int getDefaultLoadPageCount();

}
