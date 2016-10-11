package kr.co.darkkaiser.torrentad.website;

import java.util.Comparator;

public class WebSiteBoardItemDateDescTitleAscCompare implements Comparator<WebSiteBoardItem> {

	@Override
	public int compare(WebSiteBoardItem lhs, WebSiteBoardItem rhs) {
		int compareTo = lhs.getRegistDate().compareTo(rhs.getRegistDate());
		if (compareTo < 0)
			return 1;

		if (compareTo > 0)
			return -1;

		compareTo = lhs.getTitle().compareTo(rhs.getTitle());
		if (compareTo < 0)
			return -1;

		if (compareTo > 0)
			return 1;

		return 0;
	}

}
