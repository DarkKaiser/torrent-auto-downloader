package com.darkkaiser.torrentad.website;

import java.util.Comparator;

public class WebSiteBoardItemComparatorIdentifierAsc implements Comparator<WebSiteBoardItem> {

	@Override
	public int compare(final WebSiteBoardItem lhs, final WebSiteBoardItem rhs) {
		return Long.compare(lhs.getIdentifier(), rhs.getIdentifier());
	}

}
