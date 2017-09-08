package com.darkkaiser.torrentad.website;

import java.util.Comparator;

public class WebSiteBoardItemComparatorIdentifierDesc implements Comparator<WebSiteBoardItem> {

	@Override
	public int compare(final WebSiteBoardItem lhs, final WebSiteBoardItem rhs) {
		return Long.compare(rhs.getIdentifier(), lhs.getIdentifier());
	}

}
