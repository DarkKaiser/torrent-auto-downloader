package com.darkkaiser.torrentad.website.impl.totoria;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchResultData;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;

import java.util.List;

public final class TotoriaSearchResultData extends AbstractWebSiteSearchResultData {

	public TotoriaSearchResultData(final WebSiteBoard board, final String keyword, final List<WebSiteBoardItem> results) {
		super(board, keyword, results);
	}

	@Override
	public String toString() {
		return TotoriaSearchResultData.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
