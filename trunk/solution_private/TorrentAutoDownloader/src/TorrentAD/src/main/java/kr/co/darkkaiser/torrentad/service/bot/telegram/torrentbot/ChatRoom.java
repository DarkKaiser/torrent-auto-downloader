package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class ChatRoom {
	
	private WebSiteBoard board;

	// @@@@@
	private long requestId = 0;
	
	private State state = State.WAITING;
	
	// private Response response
	
	public void test() {
		this.state.execute();
	}

	public void incrementRequestId() {
		++requestId;
	}
	
	public long getRequestId() {
		return this.requestId;
	}
	
	public WebSiteBoard getBoard() {
		return board;
	}

	public void setBoard(WebSiteBoard board) {
		this.board = board;
	}

}
