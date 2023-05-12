package com.darkkaiser.torrentad.website;

import java.util.Comparator;
import java.util.Iterator;

public interface WebSiteSearchResultData {

	String getIdentifier();

	WebSiteBoard getBoard();

	String getKeyword();
	
	Iterator<WebSiteBoardItem> resultIterator(final Comparator<? super WebSiteBoardItem> comparator);

}
