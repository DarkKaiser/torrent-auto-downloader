package com.darkkaiser.torrentad.website;

import java.util.Date;
import java.util.Iterator;

public interface WebSiteBoardItem {

	WebSiteBoard getBoard();

	long getIdentifier();

	String getTitle();

	Date getRegistDate();

	String getRegistDateString();

	String getDetailPageURL();

	void addDownloadLink(final WebSiteBoardItemDownloadLink downloadLink);

	void clearDownloadLink();

	Iterator<WebSiteBoardItemDownloadLink> downloadLinkIterator();

}
