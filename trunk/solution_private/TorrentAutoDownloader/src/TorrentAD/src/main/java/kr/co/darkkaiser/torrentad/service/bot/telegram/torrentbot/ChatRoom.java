package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import java.util.concurrent.atomic.AtomicLong;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public final class ChatRoom {

	private final long chatId;

	// @@@@@ 저장 및 로드되도록
	// 조회 및 검색하려는 게시판
	private WebSiteBoard board;

	// 사용자가 조회, 검색등의 작업을 요청하였을 때, 각각의 작업을 구분하기 위한 ID
	private AtomicLong requestId = new AtomicLong(0);

	public ChatRoom(long chatId) {
		this.chatId = chatId;
	}

	public final long getChatId() {
		return this.chatId;
	}

	public long getRequestId() {
		return this.requestId.get();
	}

	public long incrementAndGetRequestId() {
		return this.requestId.incrementAndGet();
	}

	public synchronized WebSiteBoard getBoard() {
		return this.board;
	}

	public synchronized void setBoard(WebSiteBoard board) {
		if (board == null)
			throw new NullPointerException("board");

		this.board = board;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(ChatRoom.class.getSimpleName())
				.append("{")
				.append("chatId:").append(getChatId())
				.append(", board:").append(getBoard())
				.append(", requestId:").append(getRequestId())
				.append("}")
				.toString();
	}

}
