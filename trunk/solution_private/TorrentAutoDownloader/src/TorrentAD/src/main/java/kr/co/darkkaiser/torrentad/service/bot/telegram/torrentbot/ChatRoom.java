package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public final class ChatRoom {

	// 게시판 최대 검색 건수 최소/최대/기본값
	public static final int MIN_BOARD_ITEMS_LIST_COUNT = 5;
	public static final int MAX_BOARD_ITEMS_LIST_COUNT = 50;
	public static final int DEFAULT_BOARD_ITEMS_LIST_COUNT = MAX_BOARD_ITEMS_LIST_COUNT;

	// 게시판 최대 검색 건수 최소/최대/기본값
	public static final int MIN_BOARD_ITEMS_SEARCH_COUNT = 5;
	public static final int MAX_BOARD_ITEMS_SEARCH_COUNT = 50;
	public static final int DEFAULT_BOARD_ITEMS_SEARCH_COUNT = MAX_BOARD_ITEMS_SEARCH_COUNT;
	
	private final long chatId;

	// 조회 및 검색하려는 게시판
	private WebSiteBoard board;

	// 게시판 최대 조회 건수
	private int maxBoardItemsListCount = DEFAULT_BOARD_ITEMS_LIST_COUNT;

	// 게시판 최대 검색 건수
	private int maxBoardItemsSearchCount = DEFAULT_BOARD_ITEMS_SEARCH_COUNT;

	// @@@@@
	private long requestId = 0;
	
	public ChatRoom(long chatId) {
		this.chatId = chatId;
	}
	
	public long getChatId() {
		return this.chatId;
	}
	
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
		if (maxBoardItemsListCount > MAX_BOARD_ITEMS_LIST_COUNT)
			throw new ArithmeticException("Overflow maxBoardItemsListCount");
		if (maxBoardItemsListCount < MIN_BOARD_ITEMS_LIST_COUNT)
			throw new ArithmeticException("Underflow maxBoardItemsListCount");

		this.maxBoardItemsListCount = maxBoardItemsListCount;
	}

	public synchronized int getMaxBoardItemsSearchCount() {
		return this.maxBoardItemsSearchCount;
	}

	public synchronized void setMaxBoardItemsSearchCount(int maxBoardItemsSearchCount) {
		if (maxBoardItemsSearchCount > MAX_BOARD_ITEMS_SEARCH_COUNT)
			throw new ArithmeticException("Overflow maxBoardItemsSearchCount");
		if (maxBoardItemsSearchCount < MIN_BOARD_ITEMS_SEARCH_COUNT)
			throw new ArithmeticException("Underflow maxBoardItemsSearchCount");

		this.maxBoardItemsSearchCount = maxBoardItemsSearchCount;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ChatRoom.class.getSimpleName())
				.append("{")
				.append("chatId:").append(getChatId())
				.append(", board:").append(getBoard())
				.append(", maxBoardItemsListCount:").append(getMaxBoardItemsListCount())
				.append(", maxBoardItemsSearchCount:").append(getMaxBoardItemsSearchCount())
				.append("}")
				.toString();
	}

}
