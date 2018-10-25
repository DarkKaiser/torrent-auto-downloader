package com.darkkaiser.torrentad.website.impl.totoria;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteConstants;

import java.util.Objects;

public class TotoriaSearchContext extends AbstractWebSiteSearchContext {

	private TotoriaBoard board;

	private long latestDownloadBoardItemIdentifier = WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;

	public TotoriaSearchContext() {
		super(WebSite.TOTORIA);
	}

	public TotoriaBoard getBoard() {
		return this.board;
	}

	@Override
	public void setBoardName(final String name) {
		this.board = TotoriaBoard.fromString(name);
	}

	@Override
	public long getLatestDownloadBoardItemIdentifier() {
		return this.latestDownloadBoardItemIdentifier;
	}
	
	@Override
	public void setLatestDownloadBoardItemIdentifier(final long identifier) {
		this.latestDownloadBoardItemIdentifier = identifier;
	}

	@Override
	public void validate() {
		super.validate();

		Objects.requireNonNull(this.board, "board");
	}

	@Override
	public String toString() {
		return TotoriaSearchContext.class.getSimpleName() +
				"{" +
				"board:" + this.board +
				", latestDownloadBoardItemIdentifier:" + this.latestDownloadBoardItemIdentifier +
				"}, " +
				super.toString();
	}

}
