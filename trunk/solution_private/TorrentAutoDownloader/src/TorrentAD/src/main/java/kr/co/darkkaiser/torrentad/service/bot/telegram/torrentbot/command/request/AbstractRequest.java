package kr.co.darkkaiser.torrentad.service.bot.telegram.torrentbot.command.request;

import org.jsoup.helper.StringUtil;

public abstract class AbstractRequest implements Request {

	private final String identifier;

	public AbstractRequest(String identifier) {
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
				.append(AbstractRequest.class.getSimpleName())
				.append("{")
				.append("identifier:").append(getIdentifier())
				.append("}")
				.toString();
	}

}
