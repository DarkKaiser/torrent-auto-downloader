package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import kr.co.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSite;
import kr.co.darkkaiser.torrentad.website.WebSiteConstants;

public class BogoBogoSearchContext extends AbstractWebSiteSearchContext {

	private BogoBogoBoard board;

	private long latestDownloadBoardItemIdentifier = WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;

	public BogoBogoSearchContext() {
		super(WebSite.BOGOBOGO);
	}

	public BogoBogoBoard getBoard() {
		return this.board;
	}

	@Override
	public void setBoardName(String name) throws Exception {
		try {
			this.board = BogoBogoBoard.fromString(name);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public long getLatestDownloadBoardItemIdentifier() {
		return this.latestDownloadBoardItemIdentifier;
	}
	
	@Override
	public void setLatestDownloadBoardItemIdentifier(long identifier) {
		this.latestDownloadBoardItemIdentifier = identifier;
	}

	@Override
	public void validate() {
		super.validate();
		
		if (this.board == null)
			throw new NullPointerException("board");
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoSearchContext.class.getSimpleName())
				.append("{")
				.append("board:").append(this.board)
				.append(", latestDownloadBoardItemIdentifier:").append(this.latestDownloadBoardItemIdentifier)
				.append("}, ")
				.append(super.toString())
				.toString();
	}
	
}
