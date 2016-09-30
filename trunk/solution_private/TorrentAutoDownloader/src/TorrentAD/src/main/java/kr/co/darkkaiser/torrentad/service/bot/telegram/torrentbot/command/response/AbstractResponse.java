package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;

public abstract class AbstractResponse implements Response {
	
	@Override
	public String getIdentifier() {
		// @@@@@
		return null;
	}
	
	@Override
	public boolean allow(Request request) {
		// 허용가능한 request인지 반환
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractResponse.class.getSimpleName())
				.append("{")
				.append("}")
				.toString();
	}

}
