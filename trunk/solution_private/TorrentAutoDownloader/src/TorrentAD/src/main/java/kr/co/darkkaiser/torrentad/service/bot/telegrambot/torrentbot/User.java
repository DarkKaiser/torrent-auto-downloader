package kr.co.darkkaiser.torrentad.service.bot.telegrambot.torrentbot;

public class User {

	// @@@@@
	private long requestId;
	
	private State state = State.WAITING;
	
	public void test() {
		this.state.execute();
	}

}
