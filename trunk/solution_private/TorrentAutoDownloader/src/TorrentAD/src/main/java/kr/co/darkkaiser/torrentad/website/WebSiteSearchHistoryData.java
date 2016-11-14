package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

public interface WebSiteSearchHistoryData {

	long getIdentifier();

	WebSiteBoard getBoard();

	String getKeyword();
	
	Iterator<WebSiteBoardItem> resultIterator();

}
