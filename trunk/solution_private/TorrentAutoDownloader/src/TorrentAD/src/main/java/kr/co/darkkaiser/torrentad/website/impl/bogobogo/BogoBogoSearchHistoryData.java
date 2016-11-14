package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.util.List;

import kr.co.darkkaiser.torrentad.website.AbstractWebSiteSearchHistoryData;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;

public final class BogoBogoSearchHistoryData extends AbstractWebSiteSearchHistoryData {

	public BogoBogoSearchHistoryData(WebSiteBoard board, String keyword, List<WebSiteBoardItem> results) {
		super(board, keyword, results);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoSearchHistoryData.class.getSimpleName())
				.append("{")
				.append("}, ")
				.append(super.toString())
				.toString();
	}

}
