package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;
import kr.co.darkkaiser.torrentad.website.WebSiteBoardItem;

// @@@@@
public class WebSiteSearchResult {

	private final int identifier;
	
	private final WebSiteBoard board;
	
	private final String keyword;
	
	private List<WebSiteBoardItem> results = new ArrayList<>();

	public WebSiteSearchResult(int identifier, WebSiteBoard board, String keyword) {
		this.identifier = identifier;
		this.board = board;
		this.keyword = keyword;
	}
	
	public int getIdentifier() {
		return this.identifier;
	}
	
	public WebSiteBoard getBoard() {
		return this.board;
	}
	
	public String getKeyword() {
		return this.keyword;
	}

	public void add(BogoBogoBoardItem boardItem) {
		this.results.add(boardItem);
	}

	public Iterator<WebSiteBoardItem> getIterator() {
		return this.results.iterator();
	}

}
