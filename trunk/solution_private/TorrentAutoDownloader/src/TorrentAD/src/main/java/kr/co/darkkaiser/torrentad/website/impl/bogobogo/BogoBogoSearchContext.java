package kr.co.darkkaiser.torrentad.website.impl.bogobogo;

import kr.co.darkkaiser.torrentad.common.Constants;
import kr.co.darkkaiser.torrentad.website.AbstractWebSiteSearchContext;
import kr.co.darkkaiser.torrentad.website.WebSite;

public class BogoBogoSearchContext extends AbstractWebSiteSearchContext {

	private BogoBogoBoard board;

	private long latestDownloadIdentifier = Constants.INVALID_DOWNLOAD_IDENTIFIER_VALUE;

	public BogoBogoSearchContext() {
		super(WebSite.BOGOBOGO);
	}

	@Override
	public void setBoardName(String name) throws Exception {
		try {
			this.board = BogoBogoBoard.fromString(name);
		} catch (Exception e) {
			throw e;
		}
	}

	public BogoBogoBoard getBoard() {
		return this.board;
	}
	
	@Override
	public void setLatestDownloadIdentifier(long identifier) throws Exception {
		this.latestDownloadIdentifier = identifier;
	}

	public long getLatestDownloadIdentifier() {
		return this.latestDownloadIdentifier;
	}

	@Override
	public void validate() {
		super.validate();
		
		if (this.board == null) {
			throw new NullPointerException("board");
		}
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(BogoBogoSearchContext.class.getSimpleName())
				.append("{")
				.append("board:").append(this.board)
				.append(", latestDownloadIdentifier:").append(this.latestDownloadIdentifier)
				.append("}, ")
				.append(super.toString())
				.toString();
	}
	
}
