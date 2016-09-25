package kr.co.darkkaiser.torrentad.website;

public interface WebSiteBoard {

	String getName();
	
	String getDescription();
	
	String getURL();
	
	boolean hasCategory();
	
	String getDefaultRegistDateFormatString();
	
	int getDefaultLoadPageCount();

}
