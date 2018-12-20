package com.darkkaiser.torrentad.website.impl.torrentmap;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchResultData;
import com.darkkaiser.torrentad.website.WebSiteBoard;
import com.darkkaiser.torrentad.website.WebSiteBoardItem;

import java.util.List;

public final class TorrentMapSearchResultData extends AbstractWebSiteSearchResultData {

	public TorrentMapSearchResultData(final WebSiteBoard board, final String keyword, final List<WebSiteBoardItem> results) {
		super(board, keyword, results);
	}

	@Override
	public String toString() {
		return TorrentMapSearchResultData.class.getSimpleName() +
				"{" +
				"}, " +
				super.toString();
	}

}
