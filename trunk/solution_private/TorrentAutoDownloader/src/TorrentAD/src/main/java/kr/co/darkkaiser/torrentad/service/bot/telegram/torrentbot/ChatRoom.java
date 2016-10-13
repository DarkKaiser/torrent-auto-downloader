package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;
import kr.co.darkkaiser.torrentad.website.WebSiteBoard;

public class ChatRoom {
	
	private Response response;
	
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
	
	public Response getResponse() {
		return this.response;
	}

	public void setResponse(Response execute) {
		this.response = execute;
	}

	public WebSiteBoard getBoard() {
		return board;
	}

	public void setBoard(WebSiteBoard board) {
		this.board = board;
	}

}
