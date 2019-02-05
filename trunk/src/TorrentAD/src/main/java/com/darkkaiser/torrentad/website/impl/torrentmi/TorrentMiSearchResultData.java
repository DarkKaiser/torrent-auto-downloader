package com.darkkaiser.torrentad.website.impl.torrentmi;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchResultData;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;

import java.util.List;

public final class TorrentMiSearchResultData extends AbstractWebSiteSearchResultData {

	public TorrentMiSearchResultData(final WebSiteBoard board, final String keyword, final List<WebSiteBoardItem> results) {
		super(board, keyword, results);
	}

	@Override
	public String toString() {
		return TorrentMiSearchResultData.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
