package kr.co.darkkaiser.torrentad.website;

import java.util.Date;
import java.util.Iterator;

public interface WebSiteBoardItem {

	WebSiteBoard getBoard();

	long getIdentifier();

	String getTitle();

	Date getRegistDate();

	String getRegistDateString();

	Iterator<WebSiteBoardItemDownloadLink> downloadLinkIterator();
	
	void addDownloadLink(WebSiteBoardItemDownloadLink downloadLink);

	void clearDownloadLink();

}
