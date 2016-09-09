package kr.co.darkkaiser.torrentad.website;

import java.util.Date;

public interface WebSiteBoardItem {

	WebSiteBoard getBoard();

	long getIdentifier();

	String getTitle();

	Date getRegistDate();

	String getRegistDateString();

}
