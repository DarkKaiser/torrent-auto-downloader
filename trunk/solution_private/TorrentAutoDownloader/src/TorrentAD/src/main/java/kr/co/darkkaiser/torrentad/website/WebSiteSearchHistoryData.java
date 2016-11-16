package kr.co.darkkaiser.torrentad.website;

import java.util.Iterator;

public interface WebSiteSearchHistoryData {

	String getIdentifier();

	WebSiteBoard getBoard();

	String getKeyword();
	
	Iterator<WebSiteBoardItem> resultIterator();

}
