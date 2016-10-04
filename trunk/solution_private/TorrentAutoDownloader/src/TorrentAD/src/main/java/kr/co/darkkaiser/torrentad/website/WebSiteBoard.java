package kr.co.darkkaiser.torrentad.website;

public interface WebSiteBoard {

	int getId();

	String getName();
	
	String getDescription();
	
	String getURL();

	String getDefaultRegistDateFormatString();
	
	int getDefaultLoadPageCount();

}
