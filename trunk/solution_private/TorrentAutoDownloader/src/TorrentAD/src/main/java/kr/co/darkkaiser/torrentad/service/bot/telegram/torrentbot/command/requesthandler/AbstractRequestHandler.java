package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.requesthandler;

import org.jsoup.helper.StringUtil;

public abstract class AbstractRequestHandler implements RequestHandler {

	private final String identifier;

	public AbstractRequestHandler(String identifier) {
		if (StringUtil.isBlank(identifier) == true)
			throw new IllegalArgumentException("identifier는 빈 문자열을 허용하지 않습니다.");

		this.identifier = identifier;
	}

	@Override
	public String getIdentifier() {
		return this.identifier;
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(AbstractRequestHandler.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append("}")
				.toString();
	}

}
