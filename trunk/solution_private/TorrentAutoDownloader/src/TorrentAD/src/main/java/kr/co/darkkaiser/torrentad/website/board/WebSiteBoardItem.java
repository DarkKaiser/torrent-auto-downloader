package kr.co.darkkaiser.torrentad.website.board;

import java.util.Date;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public interface WebSiteBoardItem {

	WebSiteBoard getBoard();

	long getIdentifier();

	String getTitle();

	Date getRegistDate();

	String getRegistDateString();

}
