package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response.Response;

public class Chat {
	
	private Response response;

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

}
