package com.darkkaiser.torrentad.website.impl.bogobogo;

import java.util.List;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchResultData;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;

public final class BogoBogoSearchResultData extends AbstractWebSiteSearchResultData {

	public BogoBogoSearchResultData(final WebSiteBoard board, final String keyword, final List<WebSiteBoardItem> results) {
		super(board, keyword, results);
	}

	@Override
	public String toString() {
		return BogoBogoSearchResultData.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
