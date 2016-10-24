package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler.WebSiteBoardListRequestHandler;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public final class ChatRoom {

	// 조회 및 검색하려는 게시판
	private WebSiteBoard board;

	// 게시판 최대 조회 건수
	private int maxBoardItemsListCount = WebSiteBoardListRequestHandler.DEFAULT_BOARD_ITEMS_LIST_COUNT;

	// 게시판 최대 검색 건수
	private int maxBoardItemsSearchCount;

	// @@@@@
	private long requestId = 0;
	
	// @@@@@
	public void incrementRequestId() {
		++requestId;
	}
	
	// @@@@@
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

	public synchronized int getMaxBoardItemsListCount() {
		return this.maxBoardItemsListCount;
	}
	
	public synchronized void setMaxBoardItemsListCount(int maxBoardItemsListCount) {
		// @@@@@ 상수를 어디에 선언할것인가?
		if (maxBoardItemsListCount > WebSiteBoardListRequestHandler.MAX_BOARD_ITEMS_LIST_COUNT)
			throw new ArithmeticException("Overflow maxBoardItemsListCount");
		if (maxBoardItemsListCount < WebSiteBoardListRequestHandler.MIN_BOARD_ITEMS_LIST_COUNT)
			throw new ArithmeticException("Underflow maxBoardItemsListCount");

		this.maxBoardItemsListCount = maxBoardItemsListCount;
	}

	public synchronized int getMaxBoardItemsSearchCount() {
		return this.maxBoardItemsSearchCount;
	}

	public synchronized void setMaxBoardItemsSearchCount(int maxBoardItemsSearchCount) {
		// @@@@@ min, max

		this.maxBoardItemsSearchCount = maxBoardItemsSearchCount;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ChatRoom.class.getSimpleName())
				.append("{")
				.append("board:").append(getBoard())
				.append(", maxBoardItemsListCount:").append(getMaxBoardItemsListCount())
				.append(", maxBoardItemsSearchCount:").append(getMaxBoardItemsSearchCount())
				.append("}")
				.toString();
	}

}
