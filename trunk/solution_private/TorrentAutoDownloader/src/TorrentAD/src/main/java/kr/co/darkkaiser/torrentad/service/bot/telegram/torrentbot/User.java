package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.request.SearchingRequest;

public class User {

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
	
	public void execute(SearchingRequest request) {
		incrementRequestId();
		
		request.execute();
	}

}
