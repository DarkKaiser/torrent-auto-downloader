package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.response;

import org.jsoup.helper.StringUtil;

import kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request.Request;

public abstract class AbstractResponse implements Response {
	
	private final String identifier;
	
	public AbstractResponse(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}
	
	@Override
	public boolean allow(Request request) {
		// @@@@@
		// 허용가능한 request인지 반환
		return false;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractResponse.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append("}")
				.toString();
	}

}
