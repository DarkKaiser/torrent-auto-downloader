package com.darkkaiser.torrentad.website.impl.torrentmi;

import com.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;
import com.darkkaiser.torrentad.website.WebSite;
import com.darkkaiser.torrentad.website.WebSiteConstants;

import java.util.Objects;

public class TorrentMiSearchContext extends AbstractWebSiteSearchContext {

	private TorrentMiBoard board;

	private long latestDownloadBoardItemIdentifier = WebSiteConstants.INVALID_BOARD_ITEM_IDENTIFIER_VALUE;

	public TorrentMiSearchContext() {
		super(WebSite.TORRENTMI);
	}

	public TorrentMiBoard getBoard() {
		return this.board;
	}

	@Override
	public void setBoardName(final String name) {
		this.board = TorrentMiBoard.fromString(name);
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
		return TorrentMiSearchContext.class.getSimpleName() +
				"{" +
				"board:" + this.board +
				", latestDownloadBoardItemIdentifier:" + this.latestDownloadBoardItemIdentifier +
				"}, " +
				super.toString();
	}

}
