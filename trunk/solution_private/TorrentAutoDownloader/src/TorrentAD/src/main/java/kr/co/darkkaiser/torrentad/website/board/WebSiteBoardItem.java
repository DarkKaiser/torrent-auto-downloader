package kr.co.darkkaiser.torrentad.website.board;

import java.util.Date;

public interface WebSiteBoardItem {

	long getIdentifier();

	String getTitle();

	Date getRegistDate();

	String getRegistDateString();

}
