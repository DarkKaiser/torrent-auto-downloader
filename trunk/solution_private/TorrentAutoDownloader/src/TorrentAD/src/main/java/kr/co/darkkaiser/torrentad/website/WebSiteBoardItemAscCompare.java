package kr.co.darkkaiser.torrentad.website;

import java.util.Comparator;

public class WebSiteBoardItemAscCompare implements Comparator<WebSiteBoardItem> {

	@Override
	public int compare(WebSiteBoardItem lhs, WebSiteBoardItem rhs) {
		return lhs.getIdentifier() < rhs.getIdentifier() ? -1 : lhs.getIdentifier() > rhs.getIdentifier() ? 1 : 0;
	}

}
