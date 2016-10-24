package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

// @@@@@
public final class ChatRoom {

	private WebSiteBoard board;

	private int boardItemsListCount;

	// @@@@@
	private long requestId = 0;
	
	public void incrementRequestId() {
		++requestId;
	}
	
	public long getRequestId() {
		return this.requestId;
	}
	
	public synchronized WebSiteBoard getBoard() {
		return this.board;
	}

	public synchronized void setBoard(WebSiteBoard board) {
		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
	}

	public synchronized int getBoardItemsListCount() {
		return this.boardItemsListCount;
	}
	
	public synchronized void setBoardItemsListCount(int boardItemsListCount) {
		// @@@@@ min, max

		this.boardItemsListCount = boardItemsListCount;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ChatRoom.class.getSimpleName())
				.append("{")
				.append("board:").append(getBoard())
				.append(", boardItemsListCount:").append(getBoardItemsListCount())
				.append("}")
				.toString();
	}

}
