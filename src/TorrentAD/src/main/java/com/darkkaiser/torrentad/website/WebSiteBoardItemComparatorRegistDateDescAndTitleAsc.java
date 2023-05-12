package com.darkkaiser.torrentad.website;

import java.util.Comparator;

public class WebSiteBoardItemComparatorRegistDateDescAndTitleAsc implements Comparator<WebSiteBoardItem> {

	@Override
	public int compare(final WebSiteBoardItem lhs, final WebSiteBoardItem rhs) {
		int compareTo = lhs.getRegistDate().compareTo(rhs.getRegistDate());
		if (compareTo < 0)
			return 1;

		if (compareTo > 0)
			return -1;

		return Integer.compare(lhs.getTitle().compareTo(rhs.getTitle()), 0);
	}

}
